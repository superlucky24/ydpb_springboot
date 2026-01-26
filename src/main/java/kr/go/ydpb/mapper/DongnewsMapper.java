package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.DongnewsVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DongnewsMapper {
    int getTotalCount(Criteria cri);
    List<DongnewsVO> getList(Criteria cri);
    int insert(DongnewsVO board);
    void updateCount(Long dnewsId);
    DongnewsVO read(long dnewsId);
    int delete(Long dnewsId);
    int update(DongnewsVO board);
    DongnewsVO getPrev(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
    DongnewsVO getNext(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
}
