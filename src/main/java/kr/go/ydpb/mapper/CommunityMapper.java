package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityMapper {
    public int getTotalCount(Criteria cri);
    public List<CommunityVO> getList(Criteria cri);
}
