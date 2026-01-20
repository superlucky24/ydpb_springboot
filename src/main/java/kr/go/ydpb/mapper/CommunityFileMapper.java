package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.CommunityFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityFileMapper {
    void insert(CommunityFileVO file);
    CommunityFileVO read(Long fileId);
    List<CommunityFileVO> getFilesByCmntId(Long cmntId);
    void deleteFileByCmntId(Long cmntId);
    void deleteFileByFileId(Long fileId);
}
