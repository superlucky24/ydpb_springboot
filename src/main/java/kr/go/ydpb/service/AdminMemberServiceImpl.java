package kr.go.ydpb.service;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.AdminMemberMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;

    @Override
    public List<MemberVO> getMemberList(Criteria cri) {
        return adminMemberMapper.getMemberList(cri);
    }

    @Override
    public MemberVO getMemberById(String memId) {
        return adminMemberMapper.getMemberById(memId);
    }

    @Override
    public int updatePassword(String memId, String memPassword) {
        return adminMemberMapper.updatePassword(memId, memPassword);
    }

    @Override
    public int deleteMember(String memId) {
        return adminMemberMapper.deleteMember(memId);
    }

    @Override
    public int getTotalCount(Criteria cri) {
        return adminMemberMapper.getTotalCount(cri);
    }
}