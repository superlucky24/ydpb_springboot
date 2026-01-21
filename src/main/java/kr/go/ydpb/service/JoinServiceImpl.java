package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.JoinMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JoinServiceImpl implements JoinService{
    private final BCryptPasswordEncoder encoder; //암호화용 필드

    @Setter(onMethod_ = @Autowired)
    private JoinMapper joinMapper;


    @Override
    public void addMember(MemberVO vo) {

        //테스트를 위해 암호화 기능 주석 처리
        vo.setMemPassword(encoder.encode(vo.getMemPassword()));
        joinMapper.insertMember(vo);
    }

    @Override
    public boolean isIdExist(String memId) {
        return joinMapper.countById(memId) > 0;
    }

//    카카오 로그인
    @Override
    public MemberVO kakaoLoginOrJoin(KakaoUserResponse kakaoUser) {

        Long kakaoId = kakaoUser.getId();

        // 1 카카오 ID 기준 회원 조회
        MemberVO member = joinMapper.findById(kakaoId+"");

        // 2 기존 회원이면 그대로 로그인
        if (member != null) {
            return member;
        }

        // 3 신규 카카오 회원 생성
        MemberVO newMember = new MemberVO();
        newMember.setMemId(kakaoId+"");
        newMember.setLoginType("KAKAO");

        newMember.setMemName(
                kakaoUser.getKakaoAccount().getProfile().getNickname()
        );

        newMember.setMemEmail(
                kakaoUser.getKakaoAccount().getEmail()
        );

        // 비밀번호는 사용 안 함 (NOT NULL 대응)
        newMember.setMemPassword(encoder.encode(UUID.randomUUID().toString()));

        joinMapper.insertOtherMember(newMember);

        return newMember;
    }
}
