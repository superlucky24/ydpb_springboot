package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.DongNewsFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DongNewsFileMapper {
    void insert(DongNewsFileVO file);
    DongNewsFileVO read(Long fileId);
    List<DongNewsFileVO> getFiles(Long dnewsId);
    int countFiles(Long dnewsId);
    void deleteFileByBoardId(Long dnewsId);
    void deleteFileByFileId(Long fileId);
}
