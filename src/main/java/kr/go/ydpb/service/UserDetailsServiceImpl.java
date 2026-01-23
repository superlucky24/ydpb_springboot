package kr.go.ydpb.service;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final MemberMapper memberMapper;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("### loadUserByUsername CALLED");
//        MemberVO member = memberMapper.Login(username);
//        System.out.println("DB PW = " + member.getMemPassword());
//        if (member == null) {
//            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
//        }
//
//        String role = member.getMemRole() == 1
//                ? "ROLE_ADMIN"
//                : "ROLE_USER";
//
//        System.out.println("role => " + role);
//
//        return User.builder()
//                .username(member.getMemId())
//                .password(member.getMemPassword())
//                .roles(member.getMemRole() == 1 ? "ADMIN" : "USER")
//                .build();
//    }
//}
