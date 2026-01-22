package kr.go.ydpb.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsService {
    UserDetails loadLocalUser(String memId,String pw);
}
