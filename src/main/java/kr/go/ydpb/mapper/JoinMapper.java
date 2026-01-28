package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Member;

@Mapper
public interface JoinMapper {
    //DB에 회원 정보 추가
    public void insertMember(MemberVO vo);
//    아이디 중복 확인
    public int countById(String memId);

//    네이버, 카카오 로그인용
    MemberVO findById(String memId);
    void insertKaKaoMember(MemberVO member);
    void insertNaverMember(MemberVO member);
    void insertNaverKakaoMember(MemberVO member);

}
