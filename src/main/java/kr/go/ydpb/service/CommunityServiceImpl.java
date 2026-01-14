package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.CommunityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityMapper mapper;

    @Override
    public void register(CommunityVO board) {
        log.info("register service => {}", board);
        mapper.insert(board);
    }

    @Override
    public CommunityVO get(Long cmntId) {
        return null;
    }

    @Override
    public boolean modify(CommunityVO board) {
        return false;
    }

    @Override
    public boolean remove(Long cmntId) {
        return false;
    }

    @Override
    public List<CommunityVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }
}
