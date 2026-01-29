package kr.go.ydpb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//암호화 config 클래스 - 귀환
@Configuration
public class PasswordConfig {

    // 암호화에 쓰이는 BCryptPasswordEncoder 인스턴스 생성해 리턴, 스프링 빈으로 등록  - 귀환
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
