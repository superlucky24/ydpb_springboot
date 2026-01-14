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

    @Override
    public int getAllCount(Criteria cri) {
        int count = adminMapper.getAllCount(cri);
        return count;
    }

//    @Override
//    public List<ComplaintVO> getAllComplaint(int start, int end) {
//        List<ComplaintVO> list = adminMapper.getAllComplaint(start,end);
//        return list;
//    }

    @Override
    public ComplaintVO getOneComplaint(int comId) {
        ComplaintVO vo = adminMapper.getOneComplaint(comId);
        return vo;
    }

    @Override
    public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end) {
        List<ComplaintVO> list = adminMapper.searchComplaint(searchKeyword,start,end);
        return list;
    }

    @Override
    public void updateComplaint(ComplaintVO cvo) {
        adminMapper.updateComplaint(cvo);
    }

    @Override
    public void deleteComplaint(int comId) {
        adminMapper.deleteComplaint(comId);
    }

    //페이징
    @Override
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri) {
        List<ComplaintVO> list = adminMapper.getComplaintWithPaging(cri);
        return list;
    }

//    @Override
//    public MemberVO loginMember(String memId, String memPassword) {
//        MemberVO vo =  adminMapper.loginMember(memId,memPassword);
//        return vo;
//    }
}
