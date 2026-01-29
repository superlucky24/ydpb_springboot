package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GuNewsFileVO;
import kr.go.ydpb.domain.GuNewsVO;
import kr.go.ydpb.mapper.GuNewsFileMapper;
import kr.go.ydpb.mapper.GuNewsMapper;
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
public class GuNewsServiceImpl implements GuNewsService {
    @Setter(onMethod_ = @Autowired)
    private GuNewsMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private GuNewsFileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 파일저장 공통 메서드
    private void saveFile(MultipartFile file,
                          Long gnewsId,
                          String altText,
                          String insertOpt,
                          GuNewsVO board) {
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

            GuNewsFileVO fileVO = new GuNewsFileVO();
            fileVO.setUuid(uuid);
            fileVO.setFileName(originalName);
            fileVO.setUploadPath(uploadPath.getAbsolutePath());
            fileVO.setAltText(altText != null ? altText : "");
            fileVO.setInsertYn("on".equals(insertOpt) ? "Y" : "N");
            fileVO.setGnewsId(gnewsId);

            // DB에 신규 파일 정보 저장
            fileMapper.insert(fileVO);
            // 원본 게시글 VO에 파일 정보 저장
            board.getFiles().add(fileVO);

        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // 검색조건을 만족하는 게시글 총 개수
    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    // 검색조건을 만족하는 게시글 목록
    @Override
    public List<GuNewsVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    // 글등록 + 파일추가
    @Transactional
    @Override
    public void register(GuNewsVO board,
                        MultipartFile file1,
                        MultipartFile file2,
                        String fileText1,
                        String fileText2,
                        String fileOpt1,
                        String fileOpt2) {

        mapper.insertSelectKey(board);

        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, board.getGnewsId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getGnewsId(), fileText2, fileOpt2, board);
        }
    }

    // 조회수 증가
    @Override
    public void increaseCount(Long gnewsId) {
        mapper.updateCount(gnewsId);
    }

    // 글보기
    @Override
    public GuNewsVO getBoard(Long gnewsId) {
        GuNewsVO board = mapper.read(gnewsId);
        board.setFiles(fileMapper.getFiles(gnewsId));
        return board;
    }

    // 글수정 + 파일삭제
    @Transactional
    @Override
    public void updateBoard(GuNewsVO board,
                            MultipartFile file1,
                            MultipartFile file2,
                            String fileText1,
                            String fileText2,
                            String fileOpt1,
                            String fileOpt2,
                            List<Long> deleteFileIds) {
        int existingCount = fileMapper.countFiles(board.getGnewsId());
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
                GuNewsFileVO fileVO = fileMapper.read(fileId);
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
            saveFile(file1, board.getGnewsId(), fileText1, fileOpt1, board);
        }

        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, board.getGnewsId(), fileText2, fileOpt2, board);
        }

        mapper.update(board);
    }

    // 글삭제 + 파일삭제
    @Transactional
    @Override
    public int deleteBoard(Long gnewsId) {
        List<GuNewsFileVO> files = fileMapper.getFiles(gnewsId);

        if (files != null) {
            for (GuNewsFileVO f : files) {
                File file = new File(
                    f.getUploadPath(),
                    f.getUuid() + "_" + f.getFileName()
                );

                if (file.exists() && !file.delete()) {
                    log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
                }
            }
        }
        fileMapper.deleteFileByBoardId(gnewsId);

        return mapper.delete(gnewsId);
    }

    // 개별 파일정보 보기
    @Override
    public GuNewsFileVO getFile(Long fileId) {
        return fileMapper.read(fileId);
    }

    // 이전글
    @Override
    public GuNewsVO getPrev(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri) {
        return mapper.getPrev(gnewsId, cri);
    }

    // 다음글
    @Override
    public GuNewsVO getNext(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri) {
        return mapper.getNext(gnewsId, cri);
    }
}
