package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GuNewsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GuNewsMapper {
    int getTotalCount(Criteria cri);
    List<GuNewsVO> getList(Criteria cri);
    int insert(GuNewsVO board);
    void insertSelectKey(GuNewsVO board);
    void updateCount(Long gnewsId);
    GuNewsVO read(long gnewsId);
    int delete(Long gnewsId);
    int update(GuNewsVO board);
    GuNewsVO getPrev(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri);
    GuNewsVO getNext(@Param("gnewsId") Long gnewsId, @Param("cri") Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
