package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    public void register(CommunityVO board, MultipartFile file1, MultipartFile file2);
    public CommunityVO get(Long bno);
    public boolean modify(CommunityVO board);
    public boolean remove(Long bno);
    public List<CommunityVO> getList(Criteria cri);
    public int getTotal(Criteria cri);
    void increaseCount(Long cmntId);
}
