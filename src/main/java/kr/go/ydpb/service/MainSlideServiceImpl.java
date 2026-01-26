package kr.go.ydpb.service;

import kr.go.ydpb.domain.MainSlideVO;
import kr.go.ydpb.mapper.MainSlideMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MainSlideServiceImpl implements MainSlideService {

    private MainSlideMapper mapper;

    @Override
    public List<MainSlideVO> getList() {
        return mapper.getList();
    }

    @Override
    public MainSlideVO get(Long slideId) {
        return mapper.get(slideId);
    }

    @Override
    public void insert(MainSlideVO slide) {
        mapper.insert(slide);
    }

    @Override
    public void update(MainSlideVO slide) {
        mapper.update(slide);
    }

    @Override
    public void delete(Long slideId) {
        mapper.delete(slideId);
    }

    @Override
    public List<MainSlideVO> getMainList() {
        return mapper.getMainList();
    }
}
