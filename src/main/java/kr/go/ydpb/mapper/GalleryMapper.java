package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
// 포토갤러리 sql Mapper - 귀환
@Mapper
public interface GalleryMapper {

    int getTotalCount(Criteria cri); // 게시글 총 갯수
    List<GalleryVO> getList(Criteria cri); // 페이징이 적용된 게시글 목록 리스트
    void insert(GalleryVO post); // 등록
    void insertSelectKey(GalleryVO post); // SelectKey 사용 등록
    GalleryVO read(long galId); // 하나의 게시글
    int delete(Long galId); // 게시글 삭제
    int update(GalleryVO post); // 게시글 수정
    void updateCount(Long galId); // 조회수 증가

    //이전글
    GalleryVO getPrev(@Param("galId") Long galId,
                      @Param("cri") Criteria cri);
    //다음글
    GalleryVO getNext(@Param("galId") Long galId,
                      @Param("cri") Criteria cri);
    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}
