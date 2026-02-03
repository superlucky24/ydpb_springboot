package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryFileVO;
import kr.go.ydpb.domain.GalleryVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GalleryService {

    void register(
            GalleryVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2
    );

    GalleryVO get(Long galId);

    boolean modify(
            GalleryVO board,
            MultipartFile file1,
            MultipartFile file2,
            String fileText1,
            String fileText2,
            String fileOpt1,
            String fileOpt2,
            List<Long> deleteFileIds
    );

    boolean remove(Long galId);

    List<GalleryVO> getList(Criteria cri);
    int getTotal(Criteria cri);
    void increaseCount(Long galId);
    GalleryFileVO getFile(Long fileId);

    GalleryVO getPrev(Long galId, Criteria cri);
    GalleryVO getNext(Long galId, Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
