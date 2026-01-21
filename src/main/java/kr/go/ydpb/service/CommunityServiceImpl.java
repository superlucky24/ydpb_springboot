package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityFileVO;
import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.CommunityFileMapper;
import kr.go.ydpb.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper mapper;
    private final CommunityFileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // ===========================
    // 1️⃣ 게시글 등록
    // ===========================
    @Transactional
    @Override
    public void register(
            CommunityVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2
    ) {

        mapper.insertSelectKey(board);

        int uploadCount = 0;
        if (file1 != null && !file1.isEmpty()) uploadCount++;
        if (file2 != null && !file2.isEmpty()) uploadCount++;

        if (uploadCount > 2) {
            throw new IllegalStateException("파일은 최대 2개까지 업로드 가능합니다.");
        }

        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getCmntId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getCmntId(), fileText2, fileOpt2, board);
        }

        mapper.update(board);
    }

    // ===========================
    // 2️⃣ 게시글 수정
    // ===========================
    @Transactional
    @Override
    public boolean modify(
            CommunityVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2,
            List<Long> deleteFileIds
    ) {

        int existingCount = fileMapper.countByCmntId(board.getCmntId());
        int deleteCount = deleteFileIds != null ? deleteFileIds.size() : 0;

        int newCount = 0;
        if (file1 != null && !file1.isEmpty()) newCount++;
        if (file2 != null && !file2.isEmpty()) newCount++;

        if (existingCount - deleteCount + newCount > 2) {
            throw new IllegalStateException("파일은 최대 2개까지 업로드 가능합니다.");
        }

        // 1) 파일 삭제
        if (deleteFileIds != null) {
            for (Long fileId : deleteFileIds) {
                CommunityFileVO fileVO = fileMapper.read(fileId);
                if (fileVO == null) continue;

                File file = new File(
                        fileVO.getUploadPath(),
                        fileVO.getUuid() + "_" + fileVO.getFileName()
                );

                if (file.exists() && !file.delete()) {
                    log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
                }

                fileMapper.deleteFileByFileId(fileId);
            }
        }

        // 2) 새 파일 저장
        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getCmntId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getCmntId(), fileText2, fileOpt2, board);
        }

        return mapper.update(board) == 1;
    }

    // ===========================
    // 3️⃣ 게시글 삭제
    // ===========================
    @Transactional
    @Override
    public boolean remove(Long cmntId) {

        List<CommunityFileVO> files = fileMapper.getFilesByCmntId(cmntId);

        if (files != null) {
            for (CommunityFileVO f : files) {
                File file = new File(
                        f.getUploadPath(),
                        f.getUuid() + "_" + f.getFileName()
                );

                if (file.exists() && !file.delete()) {
                    log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
                }
            }
        }

        fileMapper.deleteFileByCmntId(cmntId);
        return mapper.delete(cmntId) == 1;
    }

    // ===========================
    // 파일 저장 공통 메서드
    // ===========================
    private void saveFile(
            MultipartFile file,
            Long cmntId,
            String altText,
            String insertOpt,
            CommunityVO board
    ) {

        try {
            String originalName = Paths.get(file.getOriginalFilename())
                    .getFileName()
                    .toString();

            String uuid = UUID.randomUUID().toString();
            String saveName = uuid + "_" + originalName;

            File saveFile = new File(uploadDir, saveName);
            file.transferTo(saveFile);

            CommunityFileVO fileVO = new CommunityFileVO();
            fileVO.setCmntId(cmntId);
            fileVO.setUuid(uuid);
            fileVO.setFileName(originalName);
            fileVO.setUploadPath(uploadDir);
            fileVO.setAltText(altText != null ? altText : "");
            fileVO.setInsertYn("on".equals(insertOpt) ? "Y" : "N");

            fileMapper.insert(fileVO);

            // 본문 삽입 처리: 'Y'일 경우 본문에 이미지를 삽입
            if ("Y".equals(fileVO.getInsertYn())) {

                if (board.getCmntContent() == null) {
                    board.setCmntContent("");
                }

                String imgTag = "<img src='/admin/community/download?fileId=" + fileVO.getFileId() + "' alt='" + altText + "' />";
                board.setCmntContent(board.getCmntContent() + "<br/>" + imgTag);
            }

        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }



    // ===========================
    // 기본 조회
    // ===========================
    @Override
    public CommunityVO get(Long cmntId) {
        CommunityVO board = mapper.read(cmntId);
        board.setFiles(fileMapper.getFilesByCmntId(cmntId));
        return board;
    }

    @Override
    public List<CommunityVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    @Override
    public void increaseCount(Long cmntId) {
        mapper.updateCount(cmntId);
    }

    @Override
    public CommunityFileVO getFile(Long fileId) {
        return fileMapper.read(fileId);
    }

    @Override
    public CommunityVO getPrev(Long cmntId) {
        return mapper.getPrev(cmntId);
    }

    @Override
    public CommunityVO getNext(Long cmntId) {
        return mapper.getNext(cmntId);
    }
}
