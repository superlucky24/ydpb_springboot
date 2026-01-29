package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    MemberVO Login(@Param("memId") String memId);

    // 일반회원정보 수정
    int modifyMember(MemberVO member);

    // 일반회원 비밀번호 수정
    int updatePassword(String memId, String memPassword);

}
