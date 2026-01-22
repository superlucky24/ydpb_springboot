package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;

public interface MemberService {
    public MemberVO Login(String memId,String memPassword);

    // 아이디로 회원 정보 가져오는 메서드
    public MemberVO getMemberById(String memId);

}
