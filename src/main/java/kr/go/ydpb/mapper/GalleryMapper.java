package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GalleryMapper {

    public int getTotalCount(Criteria cri);
    public List<GalleryVO> getList(Criteria cri);
    public void insert(GalleryVO post);
    public void insertSelectKey(GalleryVO post);
    public GalleryVO read(long galId);
    public int delete(Long galId);
    public int update(GalleryVO post);
    public void updateCount(Long galId);
    public GalleryVO getPrev(@Param("galId") Long galId,
                               @Param("cri") Criteria cri);

    public GalleryVO getNext(@Param("galId") Long galId,
                               @Param("cri") Criteria cri);
}
