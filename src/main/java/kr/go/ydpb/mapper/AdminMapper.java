package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//현재 미사용중인 인터페이스

@Mapper
public interface AdminMapper {
    //메서드

    // 회원
    //public void insertMember(MemberVO mvo);
    //public boolean isIdExists(String memId);
    public MemberVO loginMember(String memId, String memPassword);

}
