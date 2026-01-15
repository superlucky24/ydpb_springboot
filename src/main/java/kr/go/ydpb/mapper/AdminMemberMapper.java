package kr.go.ydpb.mapper;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberMapper {

    // 1. 회원 전체 목록
    List<MemberVO> getMemberList(Criteria cri);
    // 2. 회원 상세 정보
    MemberVO getMemberById(String memId);
    // 3. 회원 비밀번호 수정
    int updatePassword(@Param("memId") String memId, @Param("memPassword") String memPassword);
    // 4. 회원 삭제
    int deleteMember(String memId);
    // 5. 전체 데이터 개수 가져오기 (페이징 버튼 계산용)
    int getTotalCount(Criteria cri);
}
