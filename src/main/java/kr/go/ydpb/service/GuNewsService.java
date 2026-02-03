package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GuNewsFileVO;
import kr.go.ydpb.domain.GuNewsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GuNewsService {
    int getTotal(Criteria cri);
    List<GuNewsVO> getList(Criteria cri);
    void register(GuNewsVO board,
                    MultipartFile file1,
                    MultipartFile file2,
                    String fileText1,
                    String fileText2,
                    String fileOpt1,
                    String fileOpt2);
    void increaseCount(Long gnewsId);
    GuNewsVO getBoard(Long gnewsId);
    void updateBoard(GuNewsVO board,
                     MultipartFile file1,
                     MultipartFile file2,
                     String fileText1,
                     String fileText2,
                     String fileOpt1,
                     String fileOpt2,
                     List<Long> deleteFileIds);
    int deleteBoard(Long gnewsId);
    GuNewsFileVO getFile(Long fileId);
    GuNewsVO getPrev(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri);
    GuNewsVO getNext(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
