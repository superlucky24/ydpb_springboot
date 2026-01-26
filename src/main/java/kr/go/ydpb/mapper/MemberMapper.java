package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    MemberVO Login(@Param("memId") String memId);

    // 일반 회원정보 수정
    int modifyMember(MemberVO member);
    
}
