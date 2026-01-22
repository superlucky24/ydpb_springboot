package kr.go.ydpb.service;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.NaverUserResponse;
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

    private final HttpSession session;

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

        String kakaoId = kakaoUser.getId();

        // 1 카카오 ID 기준 회원 조회
        MemberVO member = joinMapper.findById(kakaoId);

        // 2 기존 회원이면 그대로 로그인
        if (member != null) {
            return member;
        }

        // 3 신규 카카오 회원 생성
        MemberVO newMember = new MemberVO();
        newMember.setMemId(kakaoId+"");
        newMember.setLoginType("KAKAO");

        newMember.setMemName(
                kakaoUser.getNickname()
        );

        newMember.setMemEmail(
                kakaoUser.getEmail()
        );

        // 비밀번호는 사용 안 함 (NOT NULL 대응)
        newMember.setMemPassword(encoder.encode(UUID.randomUUID().toString()));

        joinMapper.insertKaKaoMember(newMember);

        return newMember;
    }
// 네이버 로그인
    @Override
    public MemberVO naverLoginOrJoin(NaverUserResponse.Response naverUser) {
        MemberVO naverMember = joinMapper.findById(naverUser.getId());
        System.out.println("핸드폰 번호 : "+ naverUser.getMobile());
        String phone = naverUser.getMobile();
        if (phone != null) {
            phone = phone.replace("-", "");
        }
        if(naverMember==null){
            naverMember = new MemberVO();//조회된 값 없으면 생성

            naverMember.setMemId(naverUser.getId());
            naverMember.setMemName(naverUser.getName());
            naverMember.setMemEmail(naverUser.getEmail());
            naverMember.setMemPhone(phone);
            naverMember.setMemPassword(encoder.encode(UUID.randomUUID().toString()));

            joinMapper.insertNaverMember(naverMember);
        }

        session.setAttribute("memId",naverMember.getMemId());
        session.setAttribute("admin",naverMember.getMemRole());
        session.setAttribute("loginMember",naverMember);
        return naverMember;
    }
}
