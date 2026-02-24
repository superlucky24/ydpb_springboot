package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.domain.ComplaintVO;

import java.util.List;
import java.util.Map;

public interface ComplaintArchiveService {

     ComplaintVO getOneComplaintArchive(int comId); // 하나의 민원
     List<ComplaintArchiveVO> getAllComplaintArchive();
     int insertComplaintArchive(ComplaintVO cvo);
     void updateComplaintArchive(ComplaintVO cvo);
     void updateComplaintUserArchive(ComplaintVO cvo);
     int deleteComplaintArchive(int comId);

     // 주간 아카이브 조회를 위한 메서드
     Map<String, Object> getWeeklyArchiveWithPeriod(String targetDate);
}

