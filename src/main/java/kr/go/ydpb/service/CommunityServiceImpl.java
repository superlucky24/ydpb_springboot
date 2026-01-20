package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityFileVO;
import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.CommunityFileMapper;
import kr.go.ydpb.mapper.CommunityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityMapper mapper;

    @Autowired
    private CommunityFileMapper fileMapper;

    // 업로드 폴더 경로 (환경에 맞게 수정)
    private final String uploadDir = "C:/yoonsungmin/ydpb_springboot/upload";

    // ===========================
    // 1) 게시글 등록 (파일 포함)
    // ===========================
    @Override
    public void register(CommunityVO board,
                         MultipartFile file1,
                         MultipartFile file2,
                         String fileText1,
                         String fileText2,
                         String fileOpt1,
                         String fileOpt2) {

        // 1) 게시글 저장 (cmntId 생성)
        mapper.insertSelectKey(board);

        // 2) 파일 저장 처리
        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getCmntId(), fileText1, fileOpt1);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getCmntId(), fileText2, fileOpt2);
        }
    }

    // 파일 저장 공통 메서드
    private void saveFile(MultipartFile file,
                          Long cmntId,
                          String altText,
                          String insertOpt) {

        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String saveName = uuid + "_" + originalName;

        try {
            // 실제 파일 저장
            File saveFile = new File(uploadDir, saveName);
            file.transferTo(saveFile);

            // DB 저장
            CommunityFileVO fileVO = new CommunityFileVO();
            fileVO.setCmntId(cmntId);
            fileVO.setUuid(uuid);
            fileVO.setFileName(originalName);
            fileVO.setUploadPath(uploadDir);
            fileVO.setAltText(altText != null ? altText : "");
            fileVO.setInsertYn("on".equals(insertOpt) ? "Y" : "N");

            fileMapper.insert(fileVO);

        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // ===========================
    // 2) 게시글 수정 (파일 수정/삭제 포함)
    // ===========================
    @Override
    public boolean modify(CommunityVO board,
                          MultipartFile file1,
                          MultipartFile file2,
                          String fileText1,
                          String fileText2,
                          String fileOpt1,
                          String fileOpt2,
                          String deleteFileIds) {

        // 1) 삭제할 파일이 있으면 삭제
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            String[] ids = deleteFileIds.split(",");
            for (String id : ids) {
                Long fileId = Long.parseLong(id.trim());
                CommunityFileVO fileVO = fileMapper.read(fileId);

                // 실제 파일 삭제
                File file = new File(fileVO.getUploadPath(), fileVO.getUuid() + "_" + fileVO.getFileName());
                if (file.exists()) file.delete();

                // DB 삭제
                fileMapper.deleteFileByFileId(fileId);
            }
        }

        // 2) 새 파일 업로드 처리 (교체/추가)
        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getCmntId(), fileText1, fileOpt1);
        }
        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getCmntId(), fileText2, fileOpt2);
        }

        // 3) 게시글 수정
        return mapper.update(board) == 1;
    }

    // ===========================
    // 3) 게시글 삭제 (파일도 삭제)
    // ===========================
    @Override
    public boolean remove(Long cmntId) {

        // 1) 파일 정보 가져오기
        List<CommunityFileVO> files = fileMapper.getFilesByCmntId(cmntId);

        // 2) 실제 파일 삭제
        if (files != null) {
            for (CommunityFileVO f : files) {
                File file = new File(f.getUploadPath(), f.getUuid() + "_" + f.getFileName());
                if (file.exists()) file.delete();
            }
        }

        // 3) DB 파일 삭제
        fileMapper.deleteFileByCmntId(cmntId);

        // 4) 게시글 삭제
        return mapper.delete(cmntId) == 1;
    }

    @Override
    public CommunityVO get(Long cmntId) {
        return mapper.read(cmntId);
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
}
