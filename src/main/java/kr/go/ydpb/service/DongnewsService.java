package kr.go.ydpb.service;

import kr.go.ydpb.domain.DongnewsVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DongnewsService {
    int getTotal(Criteria cri);
    List<DongnewsVO> getList(Criteria cri);
    int insertBoard(DongnewsVO board);
    void increaseCount(Long dnewsId);
    DongnewsVO getBoard(Long dnewsId);
    void updateBoard(DongnewsVO board);
    int deleteBoard(Long dnewsId);
    DongnewsVO getPrev(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
    DongnewsVO getNext(@Param("dnewsId") Long dnewsId, @Param("cri") Criteria cri);
}
