package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;

public interface MemberService {
    public MemberVO Login(String memId,String memPassword);
}
