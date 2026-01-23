package kr.go.ydpb.config;

import kr.go.ydpb.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") //관리자 접근

                        .requestMatchers( //비로그인 접근
                                "/",                 // 메인
                                "/login",             // 커스텀 로그인 페이지
                                "/oauth2/**",         // OAuth2 인증 엔드포인트
                                "/member/**",
                                "/complaint/**",
                                "/community/**",
                                "/css/**", "/js/**", "/images/**"
//                                ,"/admin/**" // 관리자 임시
                        ).permitAll()
                        .anyRequest().authenticated()
                )
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
                .formLogin(form ->
                        form.disable()
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
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authBuilder
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder);
//
//        return authBuilder.build(); // .and() 없이 바로 build()
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       DaoAuthenticationProvider authProvider) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(authProvider)
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder)
//                .and()
//                .build();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService,
//                                                            BCryptPasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);  // setter로 주입
//        authProvider.setPasswordEncoder(passwordEncoder);     // setter로 주입
//        return authProvider;
//    }

}
