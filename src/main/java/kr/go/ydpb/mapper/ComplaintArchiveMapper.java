package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.ComplaintVO;
//import kr.go.ydpb.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;

//import java.time.LocalDate;
//import java.util.List;

@Mapper
public interface ComplaintArchiveMapper {
    //민원
//    public int getAllCount(Criteria cri); // 모든 민원 갯수 파악
    //public List<ComplaintVO> getAllComplaint(int start, int end);
//    public ComplaintVO getOneComplaintArc(int comId); // 하나의 민원
    public int insertComplaintArc(ComplaintVO cvo); // 민원 아카이브 등록
    public void updateComplaintArc(ComplaintVO cvo); //아카이브 관리자 민원 수정(답변)
    public void updateComplaintUserArc(ComplaintVO cvo); // 사용자 민원 아카이브 수정
    public void deleteComplaintArc(int comId); // 민원 삭제(아카이브에서는 삭제여부만 변경)


//    public List<ComplaintVO> getComplaintWithPaging(Criteria cri);
//    public int getAllSearchCount(Criteria cri);
//    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
