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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

// 보안 처리 config 클래스 - 귀환
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {
    private final CustomOAuth2UserService customOAuth2UserService;
//    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomLoginSuccessHandler customLoginSuccessHandler; // 주입

    @Bean // 스프링 컨테이너가 관리하는 빈으로 등록
    // SecurityFilterChain => Spring Security의 보안 처리를 담당하는 가장 핵심적인 인터페이스
    // 사용자의 요청(HTTP Request)이 서블릿에 도달하기 전, 가로채서 인증(Authentication)과 인가(Authorization)를 수행하는 필터들의 묶음
    // 수많은 필터들이 사슬처럼 동작 => 요청에 대해 어떤 필터를 적용할 것인지를 정의
    // HttpSecurity 설정을 통해 구성된 필터들의 목록을 가지고 있으며, 현재 요청이 이 필터들을 거쳐야 하는지 결정
    // Spring Security는 서블릿 컨테이너(Tomcat 등)의 필터 체인에 DelegatingFilterProxy라는 이름으로 등록
    // 이 프록시가 실제 보안 로직을 수행하는 FilterChainProxy에게 처리를 위임 => 우리가 정의한 SecurityFilterChain이 동작
    // 상속이 아닌 빈 등록 방식을 사용하여 다른 빈(예: CustomOAuth2UserService)을 주입받아 사용하기가 훨씬 자유로움
    // API 보안 체인과 일반 웹페이지용 보안 체인을 나누어 설정 가능 <- 여러개의 SecurityFilterChain을 빈으로 등록해 사용
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        // HttpSecurity HTTP 요청에 대한 보안 설정을 구성하는 메인 객체 <- 스프링이 자동으로 넣어줌
        // CustomOAuth2UserService customOAuth2UserService 소셜 로그인 성공 후 가져온 유저 정보를 처리하는 비즈니스 로직 클래스
        http // 보안 관련 설정을 메서드 체이닝으로 추가
                // authorizeHttpRequests URL별로 접근 권한을 설정
                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // 모든 요청을 다 열어둠 - 귀환  => 로그인 여부와 관계없이 모든 페이지와 API에 누구나 접근할 수 있도록 완전히 개방된 상태
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") //관리자 접근
                        .requestMatchers( //비인증 접근 처리
                                "/",                 // 메인
                                "/login",             // 커스텀 로그인 페이지
                                "/oauth2/**",         // OAuth2 인증 엔드포인트
//                                "/mypage/**",
                                "/complaint/**",
                                "/sub/**",
                                "/dongnews/**",
                                "/gunews/**",
                                "/gallery/**",
//                                "/document/**",
                                "/community/**",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // oauth2Login OAuth2 로그인 기능을 활성화
                .oauth2Login(oauth -> oauth
                        .loginPage("/login") // 사용자가 인증되지 않았을 때 이동할 커스텀 로그인 페이지 경로
                        .userInfoEndpoint(userInfo -> userInfo
                                //로그인 성공 후 유저 정보를 가져오는 지점
                                .userService(customOAuth2UserService) // 소셜 서비스(Naver, Kakao 등)로부터 받은 데이터를 처리할 서비스 객체를 지정
                        )
                        .defaultSuccessUrl("/", true) // 로그인 기능 처리 성공 시 이동 URL

                        //로그인이 성공했을 때 실행할 커스텀 로직
                        .successHandler((request, response, authentication) -> {
                            // 로그인에 성공한 사용자의 정보(객체)를 가져옴
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            //소셜 서비스에서 넘겨준 실제 유저 정보(이름, 이메일, ID 등)가 담긴 Map
                            Map<String, Object> attributes = oAuth2User.getAttributes();

                            // 세션에 데이터 바인딩용 변수 초기화
                            String memId = "";
                            String memName = "";

                            // 데이터가 어떤 형식으로 넘어오는지 응답 형식 출력 확인
                            System.out.println("OAuth2 Login Success! attributes: " + attributes);

                            // 현재 로그인을 시도한 서비스가 어디인지(naver인지 kakao인지) 구분
                            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

                            if ("naver".equals(registrationId) || "kakao".equals(registrationId)) {
                                //각 서비스의 응답 형식에 맞춰 ID와 이름을 추출
                                memId = attributes.get("id").toString();
                                memName = attributes.get("name").toString();
                            }

                            // 세션에 데이터 바인딩
                            request.getSession().setAttribute("memId", memId);
                            request.getSession().setAttribute("memName", memName);

                            // 세션에 바인딩될 데이터 출력 확인
                            System.out.println("OAuth2 Login Success! 세션에 등록된 memId: " + memId);

                            response.sendRedirect("/"); // 글쓰기 폼으로 리다이렉트
                        })
                )
                // 로그아웃 설정 추가
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃을 수행할 URL
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 URL
                        .invalidateHttpSession(true) // 로그아웃 시 서버 세션을 완전히 무효화(삭제)
                        .clearAuthentication(true) // 시큐리티 컨텍스트에 저장된 인증 정보 제거
                        .deleteCookies("JSESSIONID") // 브라우저에 남은 세션 쿠키를 삭제
                )
                .formLogin(form -> form
                        .loginPage("/login")                // 커스텀 로그인 페이지 URL
                        .loginProcessingUrl("/login/process")   // HTML Form의 action과 일치해야 함
                        .usernameParameter("memId")         // HTML input의 name (기본값: username)
                        .passwordParameter("memPassword")   // HTML input의 name (기본값: password)
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId() // 세션 고정 보호 설정
                        .maximumSessions(1) // 중복 로그인 방지
                )
                .csrf(csrf -> csrf.disable()) // CSRF 보호 기능을 끔
                .httpBasic(basic -> basic.disable()); // HTTP 기본 인증(ID/PW를 헤더에 담는 방식)을 사용하지 않음

        // 설정을 마치고 SecurityFilterChain 객체를 생성하여 리턴
        // 설정한 내용을 기반으로 실제 필터들이 담긴 SecurityFilterChain 객체가 생성
        return http.build();
    }


}
