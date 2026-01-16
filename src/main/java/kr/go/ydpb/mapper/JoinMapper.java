package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Member;

@Mapper
public interface JoinMapper {
    public void insertMember(MemberVO vo);
}
