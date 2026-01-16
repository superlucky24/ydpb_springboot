package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;

public interface JoinService {
    public void addMember(MemberVO vo);
    public boolean isIdExist(String memId);
}
