package kr.go.ydpb.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import java.util.ArrayList;
//import java.util.List;


@Controller
@AllArgsConstructor
public class MemberController {

    private MemberService service;

    @GetMapping("/login")
    public String login(@RequestParam(value = "msg", required = false) String msg,
                        @RequestParam(value = "error", required = false) String error,
                        HttpServletRequest request,   // 이전 페이지 정보를 가져오기 위해 추가
                        Authentication authentication, // 로그인 여부 확인
                        Model model) {

        /*
        // 동적값 주소
        String referer = request.getHeader("Referer");
        // 정적값 주소
        String existing = (String) session.getAttribute("prevPage");

        // 로그 확인용
        System.out.println("진입 시점 - Referer: " + referer + " / 기존 세션값: " + existing);

        // 로그인 페이지 내부에서의 이동(예: 새로고침)도 저장하지 않음
        if (referer == null || referer.contains("/login")) {
            return "member/login";
        }



        // 정적값 비어있거나 잡파일 저장시 새로운 주소 받음
        // + 메서드 이중호출시 이전 주소 지키는 보험
        if (existing == null || !referer.contains(".")) {
            // .contains(".") 은 폰트.woff, 스타일.css 처럼 확장자가 붙은 리소스를 걸러내기 위함.
            session.setAttribute("prevPage", referer);
        }
        */

        // 1. 이미 로그인된 사용자가 로그인 페이지에 접근할 경우 처리
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            // 이전 페이지 정보(Referer)
            String referer = request.getHeader("Referer");

            // 이전 페이지 정보가 있고, 자기 자신(로그인 페이지)이 아니라면 그곳으로 이동
            if (referer != null && !referer.contains("/login")) {
                return "redirect:" + referer;
            }

            // 이전 정보가 없으면 그냥 메인으로 보냅니다.
            return "redirect:/";
        }

        // 1. 로그인 필요한 서비스
        if ("auth".equals(msg)) {
            model.addAttribute("errorMsg", "로그인이 필요한 서비스입니다.");
        }
        // 2. 권한 부족 시
        if ("denied".equals(error)) {
            model.addAttribute("errorMsg", "접근 권한이 없습니다.");
        }

        // 3. 로그인 실패 시 메세지
        if ("fail".equals(error)) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }



        return "member/login";
    }

    // 시큐리티에서 처리되어 의미없어져 주석처리
//    @PostMapping("/login")
//    public String LoginProcess(@RequestParam("memId") String memId,
//                        @RequestParam("memPassword") String memPassword,
//                        HttpSession session,
//                        Model model) {
//
//        MemberVO loginMember = service.Login(memId,memPassword);
//        if (loginMember != null) {
//            session.setAttribute("memId", loginMember.getMemId());
//            session.setAttribute("memName", loginMember.getMemName());
//            session.setAttribute("admin", loginMember.getMemRole());

//          Spring Security 연동 -> Spring Security에게 로그인 성공을 알림
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority(loginMember.getMemRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER"));
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(loginMember.getMemId(), null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("로그인 권한 처리 완료 loginMember.getMemRole() => " + (loginMember.getMemRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER"));

            // 이전 페이지로 이동
//            String prevPage = (String) session.getAttribute("prevPage");
//            session.removeAttribute("prevPage"); // 사용 후 제거
//
//            return "redirect:" + (prevPage != null ? prevPage : "/");
//        }
//
//        model.addAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
//        return "member/login";
//    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 세션 전체 삭제
        return "redirect:/";    // 메인 페이지로 이동
    }

}
