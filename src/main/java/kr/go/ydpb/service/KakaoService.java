package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;

public interface KakaoService {
    String getAccessToken(String code);
    KakaoUserResponse getUserInfo(String accessToken);
}
