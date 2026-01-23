package kr.go.ydpb.service;

import kr.go.ydpb.domain.DongnewsVO;
import kr.go.ydpb.domain.Criteria;

import java.util.List;

public interface DongnewsService {
    int getTotal(Criteria cri);
    List<DongnewsVO> getList(Criteria cri);
    int insertBoard(DongnewsVO board);
    void increaseCount(Long dnewsId);
    DongnewsVO getBoard(Long dnewsId);
    void updateBoard(DongnewsVO board);
    int deleteBoard(Long dnewsId);
    DongnewsVO getPrev(Long dnewsId, Criteria cri);
    DongnewsVO getNext(Long dnewsId, Criteria cri);
}
