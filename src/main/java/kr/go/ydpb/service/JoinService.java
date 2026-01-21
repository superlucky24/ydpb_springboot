package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;

public interface JoinService {
    public void addMember(MemberVO vo);
    public boolean isIdExist(String memId);
//    카카오, 네이버 로그인 or 회원가입
    MemberVO kakaoLoginOrJoin(KakaoUserResponse kakaoUser);

}
