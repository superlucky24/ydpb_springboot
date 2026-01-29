package kr.go.ydpb.service;

import kr.go.ydpb.domain.*;
import kr.go.ydpb.mapper.DongNewsFileMapper;
import kr.go.ydpb.mapper.DongNewsMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
public class DongNewsServiceImpl implements DongNewsService {
    @Setter(onMethod_ = @Autowired)
    private DongNewsMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private DongNewsFileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 파일저장 공통 메서드
    private void saveFile(MultipartFile file,
                          Long dnewsId,
                          String altText,
                          String insertOpt,
                          DongNewsVO board) {
        try {
            String originalName = Paths.get(file.getOriginalFilename())
                    .getFileName()
                    .toString();

            String uuid = UUID.randomUUID().toString();
            String saveName = uuid + "_" + originalName;

            String basePath = System.getProperty("user.dir"); // 프로젝트 루트
            File uploadPath = new File(basePath, uploadDir);

            if (!uploadPath.exists()) {
                boolean created = uploadPath.mkdirs();
                if (!created) {
                    throw new RuntimeException("업로드 디렉토리 생성 실패");
                }
            }

            File saveFile = new File(uploadPath, saveName);

            file.transferTo(saveFile);

            DongNewsFileVO fileVO = new DongNewsFileVO();
            fileVO.setUuid(uuid);
            fileVO.setFileName(originalName);
            fileVO.setUploadPath(uploadPath.getAbsolutePath());
            fileVO.setAltText(altText != null ? altText : "");
            fileVO.setInsertYn("on".equals(insertOpt) ? "Y" : "N");
            fileVO.setDnewsId(dnewsId);

            fileMapper.insert(fileVO);
            board.getFiles().add(fileVO);

        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    @Override
    public List<DongNewsVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    @Transactional
    @Override
    public void register(DongNewsVO board,
                        MultipartFile file1,
                        MultipartFile file2,
                        String fileText1,
                        String fileText2,
                        String fileOpt1,
                        String fileOpt2) {

        mapper.insertSelectKey(board);

        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getDnewsId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getDnewsId(), fileText2, fileOpt2, board);
        }
    }

    @Override
    public void increaseCount(Long dnewsId) {
        mapper.updateCount(dnewsId);
    }

    @Override
    public DongNewsVO getBoard(Long dnewsId) {
        DongNewsVO board = mapper.read(dnewsId);
        board.setFiles(fileMapper.getFiles(dnewsId));
        return board;
    }

    @Transactional
    @Override
    public void updateBoard(DongNewsVO board,
                            MultipartFile file1,
                            MultipartFile file2,
                            String fileText1,
                            String fileText2,
                            String fileOpt1,
                            String fileOpt2,
                            List<Long> deleteFileIds) {
        int existingCount = fileMapper.countFiles(board.getDnewsId());
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
                DongNewsFileVO fileVO = fileMapper.read(fileId);
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
            saveFile(file1, board.getDnewsId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getDnewsId(), fileText2, fileOpt2, board);
        }

        mapper.update(board);
    }

    @Transactional
    @Override
    public int deleteBoard(Long dnewsId) {
        List<DongNewsFileVO> files = fileMapper.getFiles(dnewsId);

        if (files != null) {
            for (DongNewsFileVO f : files) {
                File file = new File(
                    f.getUploadPath(),
                    f.getUuid() + "_" + f.getFileName()
                );

                if (file.exists() && !file.delete()) {
                    log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
                }
            }
        }
        fileMapper.deleteFileByBoardId(dnewsId);

        return mapper.delete(dnewsId);
    }

    @Override
    public DongNewsFileVO getFile(Long fileId) {
        return fileMapper.read(fileId);
    }

    @Override
    public DongNewsVO getPrev(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri) {
        return mapper.getPrev(dnewsId, cri);
    }

    @Override
    public DongNewsVO getNext(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri) {
        return mapper.getNext(dnewsId, cri);
    }
}
