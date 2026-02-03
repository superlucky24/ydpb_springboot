package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CommunityMapper {
    public int getTotalCount(Criteria cri);
    public List<CommunityVO> getList(Criteria cri);
    public void insert(CommunityVO board);
    public void insertSelectKey(CommunityVO board);
    public CommunityVO read(long bno);
    public int delete(Long bno);
    public int update(CommunityVO board);
    public void updateCount(Long cmntId);
    public CommunityVO getPrev(@Param("cmntId") Long cmntId,
                               @Param("cri") Criteria cri);

    public CommunityVO getNext(@Param("cmntId") Long cmntId,
                               @Param("cri") Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
