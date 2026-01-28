package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryFileVO;
import kr.go.ydpb.domain.GalleryVO;
import kr.go.ydpb.mapper.GalleryFileMapper;
import kr.go.ydpb.mapper.GalleryMapper;
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
public class GalleryServiceImpl implements GalleryService {

    private final GalleryMapper mapper;
    private final GalleryFileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // =========================
    // 1️⃣ 등록
    // =========================
    @Transactional
    @Override
    public void register(
            GalleryVO board,
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
            saveFile(file1, board.getGalId(), fileText1, fileOpt1);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getGalId(), fileText2, fileOpt2);
        }

        mapper.update(board);
    }

    // =========================
    // 2️⃣ 수정
    // =========================
    @Transactional
    @Override
    public boolean modify(
            GalleryVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2,
            List<Long> deleteFileIds
    ) {

        int existingCount = fileMapper.countByGalId(board.getGalId());
        int deleteCount = deleteFileIds != null ? deleteFileIds.size() : 0;

        int newCount = 0;
        if (file1 != null && !file1.isEmpty()) newCount++;
        if (file2 != null && !file2.isEmpty()) newCount++;

        if (existingCount - deleteCount + newCount > 2) {
            throw new IllegalStateException("파일은 최대 2개까지 업로드 가능합니다.");
        }

        // 파일 삭제
        if (deleteFileIds != null) {
            for (Long fileId : deleteFileIds) {
                GalleryFileVO fileVO = fileMapper.read(fileId);
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

        // 새 파일 저장
        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getGalId(), fileText1, fileOpt1);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getGalId(), fileText2, fileOpt2);
        }

        return mapper.update(board) == 1;
    }

    // =========================
    // 3️⃣ 삭제
    // =========================
    @Transactional
    @Override
    public boolean remove(Long galId) {

        List<GalleryFileVO> files = fileMapper.getFilesByGalId(galId);

        if (files != null) {
            for (GalleryFileVO f : files) {
                File file = new File(
                        f.getUploadPath(),
                        f.getUuid() + "_" + f.getFileName()
                );

                if (file.exists() && !file.delete()) {
                    log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
                }
            }
        }

        fileMapper.deleteFileByGalId(galId);
        return mapper.delete(galId) == 1;
    }

    // =========================
    // 파일 저장 공통
    // =========================
    private void saveFile(
            MultipartFile file,
            Long galId,
            String altText,
            String insertOpt
    ) {

        try {
            String originalName = Paths.get(file.getOriginalFilename())
                    .getFileName()
                    .toString();

            String uuid = UUID.randomUUID().toString();
            String saveName = uuid + "_" + originalName;

            String basePath = System.getProperty("user.dir");
            File uploadPath = new File(basePath, uploadDir);

            if (!uploadPath.exists()) {
                boolean created = uploadPath.mkdirs();
                if (!created) throw new RuntimeException("업로드 디렉토리 생성 실패");
            }

            File saveFile = new File(uploadPath, saveName);
            file.transferTo(saveFile);

            GalleryFileVO fileVO = new GalleryFileVO();
            fileVO.setGalId(galId);
            fileVO.setUuid(uuid);
            fileVO.setFileName(originalName);
            fileVO.setUploadPath(uploadPath.getAbsolutePath());
            fileVO.setAltText(altText != null ? altText : "");
            fileVO.setInsertYn("on".equals(insertOpt) ? "Y" : "N");

            fileMapper.insert(fileVO);


        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // =========================
    // 기본 조회
    // =========================
    @Override
    public GalleryVO get(Long galId) {
        GalleryVO board = mapper.read(galId);
        board.setFiles(fileMapper.getFilesByGalId(galId));
        return board;
    }

    @Override
    public List<GalleryVO> getList(Criteria cri) {

        List<GalleryVO> list = mapper.getList(cri);

        // 각 게시글에 대해 파일 목록 채우기
        for (GalleryVO board : list) {
            List<GalleryFileVO> files = fileMapper.getFilesByGalId(board.getGalId());
            board.setFiles(files); // 파일 목록 추가
        }

        return list;
    }

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    @Override
    public void increaseCount(Long galId) {
        mapper.updateCount(galId);
    }

    @Override
    public GalleryFileVO getFile(Long fileId) {
        return fileMapper.read(fileId);
    }

    @Override
    public GalleryVO getPrev(Long galId, Criteria cri) {
        return mapper.getPrev(galId, cri);
    }

    @Override
    public GalleryVO getNext(Long galId, Criteria cri) {
        return mapper.getNext(galId, cri);
    }
}
