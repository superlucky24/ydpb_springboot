package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.mapper.ComplaintArchiveMapper;
import kr.go.ydpb.mapper.ComplaintMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ComplaintArchiveServiceImpl implements ComplaintArchiveService {
    // 민원 sql 처리 Mapper 주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintArchiveMapper complaintArchiveMapper;

    @Override
    public int insertComplaintArchive(ComplaintVO cvo) {
        return 0;
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
        // 글번호에 해당하는 민원 삭제 처리 메서드 실행
        complaintArchiveMapper.deleteComplaintArc(comId);
        // 삭제한 민원 글번호 리턴
        return comId;
    }
}
