package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final BCryptPasswordEncoder encoder; //암호화용 필드

    private MemberMapper Mapper;

    @Override
    public MemberVO Login(String memId, String memPassword) {
        MemberVO member =Mapper.Login(memId);

        // 암호화된 비밀번호와 비교 기능 추가
        boolean passCheck =encoder.matches(memPassword, member.getMemPassword());
        if(member != null
                && Objects.equals(memId, member.getMemId())
                && passCheck) { // Objects.equals(memId, member.getMemId()) ?
            return member;
        }
        return null;
    }

    // 가족관계증명서 폼 가져오기 위함
    @Override
    public MemberVO getMemberById(String memId) {

        return Mapper.Login(memId);
    }

    // 일반 회원정보 수정
    @Override
    public int modifyMember(MemberVO member) {
        return Mapper.modifyMember(member);
    }
 }
