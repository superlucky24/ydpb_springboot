package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.GuNewsFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GuNewsFileMapper {
    void insert(GuNewsFileVO file);
    GuNewsFileVO read(Long fileId);
    List<GuNewsFileVO> getFiles(Long gnewsId);
    int countFiles(Long gnewsId);
    void deleteFileByBoardId(Long gnewsId);
    void deleteFileByFileId(Long fileId);
}
