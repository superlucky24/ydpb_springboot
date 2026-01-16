package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;

import java.util.List;

public interface ComplaintService {
    public int getAllCount(Criteria cri);
    //public List<ComplaintVO> getAllComplaint(int start, int end);
    public ComplaintVO getOneComplaint(int comId);
    //public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end);
    public int insertComplaint(ComplaintVO cvo);
    public void updateComplaint(ComplaintVO cvo);
    public void updateComplaintUser(ComplaintVO cvo);
    public int deleteComplaint(int comId);
    //페이징용 목록 메서드
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri);
    public int getAllSearchCount(Criteria cri);
}
