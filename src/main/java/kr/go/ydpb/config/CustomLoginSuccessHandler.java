package kr.go.ydpb.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberMapper memberMapper;

    // 시큐리티 장부 확인용 로그
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("=== 핸들러 호출 성공! ===");
        HttpSession session = request.getSession();
        String memId = "";

        // 1. 로그인 타입에 따른 memId 추출
        if (authentication.getPrincipal() instanceof UserDetails) {
            // 일반 로그인
            memId = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            // 소셜 로그인 (OAuth2User)
            // OAuth2LoginSuccessHandler 로직을 여기에 통합
            org.springframework.security.oauth2.core.user.OAuth2User oAuth2User =
                    (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            // 네이버/카카오 구조에 맞춰 ID 추출 (기존 SecurityConfig에 있던 로직)
            if (attributes.get("id") != null) {
                memId = attributes.get("id").toString();
            } else if (attributes.get("sub") != null) { // 구글 등
                memId = attributes.get("sub").toString();
            }
        }

        // 2. DB 정보 조회 및 세션 바인딩
        if (!memId.isEmpty()) {
            MemberVO member = memberMapper.Login(memId);
            if (member != null) {
                session.setAttribute("memId", memId);
                session.setAttribute("member", member);
                session.setAttribute("admin", member.getMemRole());
                session.setAttribute("memName", member.getMemName());
                log.info("세션 데이터 바인딩 완료: {}", memId);
            }
        }

        /*
        // 로그인한 사용자 정보 가져오기 UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String memId = userDetails.getUsername();
        MemberVO member = memberMapper.Login(memId);
        log.info("CustomLoginSuccessHandler memId: {}", memId);

        // 세션에 데이터 바인딩
        session.setAttribute("memId", memId);
        session.setAttribute("member", member);
        session.setAttribute("admin", member.getMemRole());
        session.setAttribute("memName",member.getMemName());

        // UserDetails에 memName을 커스텀하게 추가했다면 바로 꺼낼 수 있음
        // 그렇지 않다면 여기서 DB를 한 번 더 조회하거나
        // 권한 정보를 활용할 수 있음
        // 예: session.setAttribute("memName", "홍길동");
        */

        // 시큐리티 장부
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        log.info("=== 로그인 성공 핸들러 분석 시작 ===");
        if (savedRequest != null) {
            // 1. 시큐리티 장부가 있다면 (강제로 튕겨온 경우), 시큐리티 본래의 로직에 맡깁니다.
            log.info("시큐리티 장부 발견, 해당 위치로 이동: {}", savedRequest.getRedirectUrl());
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            // 2. 장부가 없다면 (직접 로그인 버튼 클릭), 우리가 저장한 prevPage를 확인합니다.
            String prevPage = (String) session.getAttribute("prevPage");
            if (prevPage != null && !prevPage.isEmpty()) {
                log.info("장부 없음, 세션의 prevPage로 이동: {}", prevPage);
                session.removeAttribute("prevPage"); // 사용 후 삭제
                getRedirectStrategy().sendRedirect(request, response, prevPage);
            } else {
                // 3. 둘 다 없으면 홈으로
                log.info("이동할 정보 없음, 홈으로 이동");
                getRedirectStrategy().sendRedirect(request, response, "/");
            }
        }


//        // 로그인 성공 후 이동할 페이지 설정
//        // 시큐리티가 제공하는 기본 로그인 성공 로직을 실행하라는 코드
//        // 장부에 주소가 없으면 이동할 기본 페이지 설정
//        setDefaultTargetUrl("/");
//        super.onAuthenticationSuccess(request, response, authentication);
    }
}