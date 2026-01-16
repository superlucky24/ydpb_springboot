package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private MemberMapper Mapper;

    @Override
    public MemberVO Login(String memId, String memPassword) {
        MemberVO member = Mapper.Login(memId);
        if(member != null
                && Objects.equals(memId, member.getMemId())
                && Objects.equals(memPassword, member.getMemPassword())) {
            return member;
        }
        return null;
    }
}
