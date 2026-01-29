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
public class ComplaintServiceImpl implements ComplaintService { // 인터페이스 구현
    // 민원 sql 처리 Mapper 주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintMapper complaintMapper;

    // 모든 민원 갯수 파악
    @Override
    public int getAllCount(Criteria cri) {
        // Mapper 메서드 실행
        int count = complaintMapper.getAllCount(cri);
        // 갯수 리턴
        return count;
    }

//    @Override
//    public List<ComplaintVO> getAllComplaint(int start, int end) {
//        List<ComplaintVO> list = adminMapper.getAllComplaint(start,end);
//        return list;
//    }
    // 하나의 민원 상세보기
    @Override
    public ComplaintVO getOneComplaint(int comId) {
        // 하나의 민원 조회
        ComplaintVO vo = complaintMapper.getOneComplaint(comId);
        // 하나의 민원 리턴
        return vo;
    }

//    @Override
//    public List<ComplaintVO> searchComplaint(String searchKeyword, int start, int end) {
//        List<ComplaintVO> list = complaintMapper.searchComplaint(searchKeyword,start,end);
//        return list;
//    }

    // 관리자 민원 수정
    @Override
    public void updateComplaint(ComplaintVO cvo) {
        complaintMapper.updateComplaint(cvo);
    }

    // 민원 삭제
    @Override
    public int deleteComplaint(int comId) {
        // 글번호에 해당하는 민원 삭제 메서드 실행
        complaintMapper.deleteComplaint(comId);
        // 삭제한 민원 글번호 리턴
        return comId;
    }

    //페이징 목록
    @Override
    public List<ComplaintVO> getComplaintWithPaging(Criteria cri) {
        // 페이징 단위에 따라 게시글 리스트를 가져오는 메서드 실행
        List<ComplaintVO> list = complaintMapper.getComplaintWithPaging(cri);
        // 리스트 리턴
        return list;
    }

    // 검색 시 해당 민원 갯수 가져오는 메서드
    @Override
    public int getAllSearchCount(Criteria cri) {
        int total = complaintMapper.getAllSearchCount(cri);
        return total;
    }

    //민원 등록
    @Override
    public int insertComplaint(ComplaintVO cvo) {
        return complaintMapper.insertComplaint(cvo);
    }

    //사용자 민원 수정
    @Override
    public void updateComplaintUser(ComplaintVO cvo) {
        complaintMapper.updateComplaintUser(cvo);
    }
}
