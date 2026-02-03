package kr.go.ydpb.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final MemberMapper memberMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("=== 핸들러 호출 성공! ===");
        HttpSession session = request.getSession();

        // 로그인한 사용자 정보 가져오기 UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String memId = userDetails.getUsername();
        MemberVO member = memberMapper.Login(memId);
        System.out.println("CustomLoginSuccessHandler memId: " + memId);

        // 세션에 데이터 바인딩
        session.setAttribute("memId", memId);
        session.setAttribute("member", member);
        session.setAttribute("admin", member.getMemRole());
        session.setAttribute("memName",member.getMemName());

        // UserDetails에 memName을 커스텀하게 추가했다면 바로 꺼낼 수 있음
        // 그렇지 않다면 여기서 DB를 한 번 더 조회하거나
        // 권한 정보를 활용할 수 있음
        // 예: session.setAttribute("memName", "홍길동");

        System.out.println("일반 로그인 성공! 세션 등록 memId: " + memId);

        // 로그인 성공 후 이동할 페이지 설정
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}