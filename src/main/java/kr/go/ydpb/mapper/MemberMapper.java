package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    // 1. 회원 전체 목록 (리스트 띄우기)
    List<MemberVO> getMemberList(@Param("type") String type, @Param("keyword") String keyword);


    // 2. 회원 상세 정보 (클릭했을 때 상세 보기)
    MemberVO getMemberById(String memId);

    // 3. 회원 정보 수정 (상세보기에서 수정 누를 때)
    int updatePassword(@Param("memId") String memId, @Param("memPassword") String memPassword);

    // 4. 회원 삭제 (상세보기에서 삭제 누를 때)
    int deleteMember(String memId);
}
