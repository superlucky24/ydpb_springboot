package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.domain.ComplaintVO;

import java.util.List;

public interface ComplaintArchiveService {

     ComplaintVO getOneComplaintArchive(int comId); // 하나의 민원
     List<ComplaintArchiveVO> getAllComplaintArchive();
     int insertComplaintArchive(ComplaintVO cvo);
     void updateComplaintArchive(ComplaintVO cvo);
     void updateComplaintUserArchive(ComplaintVO cvo);
     int deleteComplaintArchive(int comId);
}
