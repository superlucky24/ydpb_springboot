package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final BCryptPasswordEncoder encoder; //암호화용 필드
    private final PasswordEncoder passwordEncoder;

    private MemberMapper Mapper;

    @Override
    public MemberVO Login(String memId, String memPassword) {
        MemberVO member =Mapper.Login(memId);

        // 암호화된 비밀번호와 비교 기능 추가

        if(member != null) { // Objects.equals(memId, member.getMemId()) ?
            boolean passCheck =encoder.matches(memPassword, member.getMemPassword());
            if(Objects.equals(memId, member.getMemId())
                    && passCheck){
                return member;
            }
        }
        return null;
    }

    // 가족관계증명서 폼 가져오기 위함
    @Override
    public MemberVO getMemberById(String memId) {

        return Mapper.Login(memId);
    }

    // 일반회원정보 수정
    @Override
    public int modifyMember(MemberVO member) {
        System.out.println("modify member birth : "+member.getMemBirth());
        System.out.println("modify member gender : "+member.getMemGender());
        System.out.println("modify member Address : "+member.getMemAddress());
        System.out.println("modify member AddressDetail : "+member.getMemAddressDetail());
        System.out.println("modify member Tel : "+member.getMemTel());
        System.out.println("modify member Phone : "+member.getMemPhone());
        System.out.println("modify member Email : "+member.getMemEmail());
        return Mapper.modifyMember(member);
    }

    // 일반회원 비밀번호 수정
    @Override
    public int updatePassword(String memId, String memPassword) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memPassword);

        // 암호화된 비밀번호 저장
        return Mapper.updatePassword(memId, encodedPassword);
    }
 }
