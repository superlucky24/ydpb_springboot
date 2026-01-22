package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsServiceImpl implements UserDetailsService{
    private MemberMapper memberMapper;
    @Override
    public UserDetails loadLocalUser(String memId, String pw) {
        MemberVO member = memberMapper.Login(memId);

        String role = member.getMemRole() == 1
                ? "ROLE_ADMIN"
                : "ROLE_USER";

        return User.builder()
                .username(member.getMemId())
                .password(member.getMemPassword())
                .authorities(role)
                .build();
    }
}
