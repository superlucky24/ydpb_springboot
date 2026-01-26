package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MainSlideVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainSlideMapper {
    List<MainSlideVO> getList();

    MainSlideVO get(Long slideId);

    void insert(MainSlideVO slide);

    void update(MainSlideVO slide);

    void delete(Long slideId);
}
