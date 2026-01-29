package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.GalleryFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GalleryFileMapper {

    void insert(GalleryFileVO file);

    GalleryFileVO read(Long fileId);

    List<GalleryFileVO> getFilesByGalId(Long galId);

    void deleteFileByGalId(Long galId);

    void deleteFileByFileId(Long fileId);

    int countByGalId(Long galId);
}
