package kr.go.ydpb.controller;


import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@AllArgsConstructor
public class MemberController {

    private MemberService service;

    @GetMapping("/login")
    public String Login() {
        return "member/login"; // 로그인 HTML 경로
    }

    @PostMapping("/login")
    public String LoginProcess(@RequestParam("memId") String memId,
                        @RequestParam("memPassword") String memPassword,
                        HttpSession session,
                        Model model) {

        MemberVO loginMember = service.Login(memId,memPassword);
        if (loginMember != null) {
            session.setAttribute("memId", loginMember.getMemId());
            session.setAttribute("admin", loginMember.getMemRole());
            return "redirect:/"; // 메인 페이지 이동
        }

        model.addAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
        return "member/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 세션 전체 삭제
        return "redirect:/";    // 메인 페이지로 이동
    }


}
