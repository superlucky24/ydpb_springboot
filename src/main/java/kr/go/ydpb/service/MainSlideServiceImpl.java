package kr.go.ydpb.service;

import kr.go.ydpb.domain.MainSlideVO;
import kr.go.ydpb.mapper.MainSlideMapper;

import java.util.List;

public class MainSlideServiceImpl implements MainSlideService {

    private MainSlideMapper mapper;

    @Override
    public List<MainSlideVO> getSlides() {
        return mapper.getSlideList();
    }

    @Override
    public void insertSlide(MainSlideVO slide) {
        mapper.insert(slide);
    }
}
