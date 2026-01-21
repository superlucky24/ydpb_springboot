package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.mapper.ComplaintMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    @Setter(onMethod_ = @Autowired)
    private ComplaintMapper complaintMapper;

    @Override
    public int getAllCount(Criteria cri) {
        int count = complaintMapper.getAllCount(cri);
        return count;
    }

//    @Override
//    public List<ComplaintVO> getAllComplaint(int start, int end) {
//        List<ComplaintVO> list = adminMapper.getAllComplaint(start,end);
//        return list;
//    }

    @Override
    public ComplaintVO getOneComplaint(int comId) {
        ComplaintVO vo = complaintMapper.getOneComplaint(comId);
        return vo;
    }

//    @Override
//    public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end) {
//        List<ComplaintVO> list = complaintMapper.searchComplaint(searchKeyword,start,end);
//        return list;
//    }

    @Override
    public void updateComplaint(ComplaintVO cvo) {
        complaintMapper.updateComplaint(cvo);
    }

    @Override
    public int deleteComplaint(int comId) {
        complaintMapper.deleteComplaint(comId);
        return comId;
    }

    //페이징
    @Override
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri) {
        List<ComplaintVO> list = complaintMapper.getComplaintWithPaging(cri);
        return list;
    }

    @Override
    public int getAllSearchCount(Criteria cri) {
        int total = complaintMapper.getAllSearchCount(cri);
        return total;
    }

    @Override
    public int insertComplaint(ComplaintVO cvo) {
        return complaintMapper.insertComplaint(cvo);
    }

    @Override
    public void updateComplaintUser(ComplaintVO cvo) {
        complaintMapper.updateComplaintUser(cvo);
    }
}
