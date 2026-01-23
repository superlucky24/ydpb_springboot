package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.DongnewsVO;
import kr.go.ydpb.mapper.DongnewsMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DongnewsServiceImpl implements DongnewsService {
    @Setter(onMethod_ = @Autowired)
    private DongnewsMapper mapper;

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    @Override
    public List<DongnewsVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    @Override
    public int insertBoard(DongnewsVO board) {
        return mapper.insert(board);
    }

    @Override
    public void increaseCount(Long dnewsId) {
        mapper.updateCount(dnewsId);
    }

    @Override
    public DongnewsVO getBoard(Long dnewsId) {
        return mapper.read(dnewsId);
    }

    @Override
    public void updateBoard(DongnewsVO board) {
        mapper.update(board);
    }

    @Override
    public int deleteBoard(Long dnewsId) {
        return mapper.delete(dnewsId);
    }

    @Override
    public DongnewsVO getPrev(Long dnewsId, Criteria cri) {
        return mapper.getPrev(dnewsId, cri);
    }

    @Override
    public DongnewsVO getNext(Long dnewsId, Criteria cri) {
        return mapper.getNext(dnewsId, cri);
    }
}
