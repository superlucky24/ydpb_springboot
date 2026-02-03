package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;

import java.time.LocalDate;
import java.util.List;

public interface AdminMemberService {

    // 1. 회원 전체 목록
    List<MemberVO> getMemberList(Criteria cri);

    // 2. 회원 상세 정보
    MemberVO getMemberById(String memId);

    // 3. 비밀번호 수정
    int updatePassword(String memId, String memPassword);

    // 4. 회원 삭제
    int deleteMember(String memId);

    // 5. 전체 데이터 개수
    int getTotalCount(Criteria cri);

    int getCountPeriod(LocalDate startDate, LocalDate endDate);
}