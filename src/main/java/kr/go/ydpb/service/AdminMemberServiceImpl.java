package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.AdminMemberMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // 페이징 및 검색 조건에 따른 회원 목록 조회
    @Override
    public List<MemberVO> getMemberList(Criteria cri) {
        return adminMemberMapper.getMemberList(cri);
    }

    @Override
    public MemberVO getMemberById(String memId) {
        return adminMemberMapper.getMemberById(memId);
    }

    // 관리자가 회원 비밀번호 강제로 수정 후 암호화하여 저장
    @Override
    public int updatePassword(String memId, String memPassword) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memPassword);

        // 암호화된 비밀번호 저장
        return adminMemberMapper.updatePassword(memId, encodedPassword);
    }

    @Override
    public int deleteMember(String memId) {
        return adminMemberMapper.deleteMember(memId);
    }

    @Override
    public int getTotalCount(Criteria cri) {
        return adminMemberMapper.getTotalCount(cri);
    }

    // 일정 기간 개수 조회
    @Override
    public int getCountPeriod(LocalDate startDate, LocalDate endDate) {
        return adminMemberMapper.getCountPeriod(startDate, endDate);
    }
}