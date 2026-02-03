package kr.go.ydpb.service;

import kr.go.ydpb.domain.DongNewsFileVO;
import kr.go.ydpb.domain.DongNewsVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DongNewsService {
    int getTotal(Criteria cri);
    List<DongNewsVO> getList(Criteria cri);
    void register(DongNewsVO board,
                    MultipartFile file1,
                    MultipartFile file2,
                    String fileText1,
                    String fileText2,
                    String fileOpt1,
                    String fileOpt2);
    void increaseCount(Long dnewsId);
    DongNewsVO getBoard(Long dnewsId);
    void updateBoard(DongNewsVO board,
                     MultipartFile file1,
                     MultipartFile file2,
                     String fileText1,
                     String fileText2,
                     String fileOpt1,
                     String fileOpt2,
                     List<Long> deleteFileIds);
    int deleteBoard(Long dnewsId);
    DongNewsFileVO getFile(Long fileId);
    DongNewsVO getPrev(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
    DongNewsVO getNext(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
