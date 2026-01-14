package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    @Override
    public void register(CommunityVO board) {

    }

    @Override
    public CommunityVO get(Long bno) {
        return null;
    }

    @Override
    public boolean modify(CommunityVO board) {
        return false;
    }

    @Override
    public boolean remove(Long bno) {
        return false;
    }

    @Override
    public List<CommunityVO> getList(Criteria cri) {
        return List.of();
    }

    @Override
    public int getTotal(Criteria cri) {
        return 0;
    }
}
