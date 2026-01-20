package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.CommunityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityMapper mapper;

    @Override
    public void register(CommunityVO board, MultipartFile uploadFile) {
        mapper.insert(board);
    }

    @Override
    public CommunityVO get(Long cmntId) {
        return mapper.read(cmntId);
    }

    @Override
    public boolean modify(CommunityVO board) {
        return mapper.update(board) == 1;
    }

    @Override
    public boolean remove(Long cmntId) {
        return mapper.delete(cmntId) == 1;
    }

    @Override
    public List<CommunityVO> getList(Criteria cri) {
        return mapper.getList(cri);
    }

    @Override
    public int getTotal(Criteria cri) {
        return mapper.getTotalCount(cri);
    }

    @Override
    public void increaseCount(Long cmntId) {
        mapper.updateCount(cmntId);
    }
}
