package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
@Mapper
public interface ComplaintMapper {
    //민원
    public int getAllCount(Criteria cri); // 모든 민원 갯수 파악
    //public List<ComplaintVO> getAllComplaint(int start, int end);
    public ComplaintVO getOneComplaint(int comId); // 하나의 민원
    //public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end);
    public int insertComplaint(ComplaintVO cvo); // 민원 등록
    public void updateComplaint(ComplaintVO cvo); //관리자 민원 수정(답변)
    public void updateComplaintUser(ComplaintVO cvo); // 사용자 민원 수정
    public void deleteComplaint(int comId); // 민원 삭제
    //페이징용 목록 메서드
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri);
    public int getAllSearchCount(Criteria cri); // 검색 결과 갯수 파악
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
