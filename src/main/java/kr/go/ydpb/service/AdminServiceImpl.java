package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.AdminMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{
    @Setter(onMethod_ = @Autowired)
    private AdminMapper adminMapper;




//    @Override
//    public MemberVO loginMember(String memId, String memPassword) {
//        MemberVO vo =  adminMapper.loginMember(memId,memPassword);
//        return vo;
//    }
}
