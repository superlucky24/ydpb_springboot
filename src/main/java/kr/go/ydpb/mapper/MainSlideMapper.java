package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MainSlideVO;

import java.util.List;

public interface MainSlideMapper {
    List<MainSlideVO> getSlideList();
    void insert(MainSlideVO slide);
}
