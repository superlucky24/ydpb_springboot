package kr.go.ydpb.config;

import kr.go.ydpb.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 보안 처리 config 클래스
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {
    private final CustomOAuth2UserService customOAuth2UserService;
//    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") //관리자 접근
                        .anyRequest().permitAll() // 나머지는 다 열어둠
//                        .requestMatchers( //비로그인 접근
//                                "/",                 // 메인
//                                "/login",             // 커스텀 로그인 페이지
//                                "/oauth2/**",         // OAuth2 인증 엔드포인트
//                                "/member/**",
//                                "/complaint/**",
//                                "/community/**",
//                                "/css/**", "/js/**", "/images/**"
//                                ,"/admin/**" // 관리자 임시
//                        ).permitAll()
                )
//                .exceptionHandling(exc -> exc
//                        // 권한 없는 사람이 올 때 (일반유저가 /admin 올 때)
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.sendRedirect("/?error=denied");
//                        })
//                        // 인증 안 된 사람이 올 때 (Security가 로그인 정보를 모를 때)
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            // 여기서 로그인 페이지로 보내지 말고, 메인으로 보내거나
//                            // 기존 Interceptor가 처리하도록 그냥 둡니다.
//                            response.sendRedirect("/");
//                        })
//                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .formLogin(form -> form
                          .disable()
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//
//                        .usernameParameter("memId") // HTML의 <input name="memId">와 일치해야 함
//                        .passwordParameter("memPassword") // HTML의 <input name="memPassword">와 일치해야 함
//
//                        .defaultSuccessUrl("/", true)
//                        .failureHandler((request, response, exception) -> {
//                            System.out.println("로그인 실패 이유: " + exception.getMessage());
//                            response.sendRedirect("/login?error");
//                        })
//                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }


//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       DaoAuthenticationProvider authProvider) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(authProvider)
//                .build();
//    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService,
//                                                            BCryptPasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);  // setter로 주입
//        authProvider.setPasswordEncoder(passwordEncoder);     // setter로 주입
//        return authProvider;
//    }

}
