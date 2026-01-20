package kr.go.ydpb.service;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.CommunityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityMapper mapper;

    @Override
    public void register(CommunityVO board, MultipartFile file1, MultipartFile file2) {
        mapper.insert(board);

        String uploadDir = "C:/yoonsungmin/ydpb_springboot/upload";

        if (file1 != null && !file1.isEmpty()) {
            saveFile(file1, uploadDir);
        }
        if (file2 != null && !file2.isEmpty()) {
            saveFile(file2, uploadDir);
        }
    }

    private void saveFile(MultipartFile file, String uploadDir) {
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String saveName = uuid + "_" + originalName;

        try {
            file.transferTo(new File(uploadDir, saveName));
            log.info("파일 저장 완료: {}", saveName);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
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
