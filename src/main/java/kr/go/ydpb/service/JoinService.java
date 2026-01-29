package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.NaverUserResponse;

public interface JoinService {
    public void addMember(MemberVO vo); // 회원 등록
    public boolean isIdExist(String memId); // 중복확인
//    카카오, 네이버 로그인 or 회원가입
    MemberVO kakaoLoginOrJoin(KakaoUserResponse kakaoUser);
    MemberVO naverLoginOrJoin(NaverUserResponse.Response naverUser);
}
