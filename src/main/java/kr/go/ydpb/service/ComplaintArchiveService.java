package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintVO;

public interface ComplaintArchiveService {
    public int insertComplaintArchive(ComplaintVO cvo);
    public void updateComplaintArchive(ComplaintVO cvo);
    public void updateComplaintUserArchive(ComplaintVO cvo);
    public int deleteComplaintArchive(int comId);
}
