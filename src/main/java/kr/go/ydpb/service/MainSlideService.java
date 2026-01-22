package kr.go.ydpb.service;

import kr.go.ydpb.domain.MainSlideVO;

import java.util.List;

public interface MainSlideService {
    public List<MainSlideVO> getSlides();
    public void insertSlide(MainSlideVO slide);
}
