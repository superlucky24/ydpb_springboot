package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {

    void register(
            CommunityVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2
    );

    CommunityVO get(Long cmntId);

    boolean modify(
            CommunityVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2,
            List<Long> deleteFileIds
    );

    boolean remove(Long cmntId);

    List<CommunityVO> getList(Criteria cri);
    int getTotal(Criteria cri);
    void increaseCount(Long cmntId);
}
