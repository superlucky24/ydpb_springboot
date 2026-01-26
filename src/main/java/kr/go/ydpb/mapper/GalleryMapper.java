package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GalleryMapper {

    int getTotalCount(Criteria cri);
    List<GalleryVO> getList(Criteria cri);
    void insert(GalleryVO post);
    void insertSelectKey(GalleryVO post);
    GalleryVO read(long galId);
    int delete(Long galId);
    int update(GalleryVO post);
    void updateCount(Long galId);
    GalleryVO getPrev(@Param("galId") Long galId,
                               @Param("cri") Criteria cri);

    GalleryVO getNext(@Param("galId") Long galId,
                               @Param("cri") Criteria cri);
}
