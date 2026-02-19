package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.mapper.ComplaintArchiveMapper;
import kr.go.ydpb.mapper.ComplaintMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ComplaintArchiveServiceImpl implements ComplaintArchiveService {
    // 민원 아카이브 sql 처리 Mapper 주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintArchiveMapper complaintArchiveMapper;

    @Override
    public ComplaintVO getOneComplaintArchive(int comId) {
        // 하나의 민원 아카이브 조회
        ComplaintVO vo = complaintArchiveMapper.getOneComplaintArc(comId);
        // 하나의 민원 아카이브 리턴
        return vo;
    }

    @Override
    public List<ComplaintArchiveVO> getAllComplaintArchive() {
        List<ComplaintArchiveVO> list = complaintArchiveMapper.getAllComplaintArc();
        return list;
    }

    @Override
    public int insertComplaintArchive(ComplaintVO cvo) {
        return complaintArchiveMapper.insertComplaintArc(cvo);
    }

    @Override
    public void updateComplaintArchive(ComplaintVO cvo) {
        complaintArchiveMapper.updateComplaintArc(cvo);
    }

    @Override
    public void updateComplaintUserArchive(ComplaintVO cvo) {
        complaintArchiveMapper.updateComplaintUserArc(cvo);
    }

    @Override
    public int deleteComplaintArchive(int comId) {
        // 글번호에 해당하는 민원 아카이브 삭제 처리 메서드 실행
        complaintArchiveMapper.deleteComplaintArc(comId);
        // 삭제한 민원 아카이브 글번호 리턴
        return comId;
    }
}
