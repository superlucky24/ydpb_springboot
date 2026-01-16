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
        boolean passCheck =encoder.matches(memPassword, member.getMemPassword()); // 입력받은 값과 해싱된 비밀번호 비교
        if(member != null
                && Objects.equals(memId, member.getMemId())
                && memPassword.equals(member.getMemPassword())) { // Objects.equals(memId, member.getMemId()) ?
            return member;
        }
        return null;
    }
}
