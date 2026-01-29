package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.DongNewsVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DongNewsMapper {
    int getTotalCount(Criteria cri);
    List<DongNewsVO> getList(Criteria cri);
    int insert(DongNewsVO board);
    void insertSelectKey(DongNewsVO board);
    void updateCount(Long dnewsId);
    DongNewsVO read(long dnewsId);
    int delete(Long dnewsId);
    int update(DongNewsVO board);
    DongNewsVO getPrev(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
    DongNewsVO getNext(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
}
