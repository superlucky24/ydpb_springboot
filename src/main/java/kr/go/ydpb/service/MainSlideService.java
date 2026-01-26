package kr.go.ydpb.service;

import kr.go.ydpb.domain.MainSlideVO;

import java.util.List;

public interface MainSlideService {
    public List<MainSlideVO> getList();
    public MainSlideVO get(Long slideId);
    public void insert(MainSlideVO slide);
    public void update(MainSlideVO slide);
    public void delete(Long slideId);
}
