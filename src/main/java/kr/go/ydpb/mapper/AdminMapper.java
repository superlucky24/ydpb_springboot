package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    //메서드

    //민원
    public int getAllCount(Criteria cri);
    //public List<ComplaintVO> getAllComplaint(int start, int end);
    public ComplaintVO getOneComplaint(int comId);
    public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end);
    //public int insertComplaint(ComplaintVO cvo);
    public void updateComplaint(ComplaintVO cvo);
    public void deleteComplaint(int comId);
    //페이징용 목록 메서드
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri);

    // 회원
    //public void insertMember(MemberVO mvo);
    //public boolean isIdExists(String memId);
    public MemberVO loginMember(String memId, String memPassword);

}
