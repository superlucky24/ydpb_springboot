package kr.go.ydpb.controller;


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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import java.util.ArrayList;
//import java.util.List;


@Controller
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;
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
            session.setAttribute("memName", loginMember.getMemName());
            session.setAttribute("admin", loginMember.getMemRole());

//          Spring Security 연동 -> Spring Security에게 로그인 성공을 알림
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority(loginMember.getMemRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER"));
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(loginMember.getMemId(), null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("로그인 권한 처리 완료 loginMember.getMemRole() => " + (loginMember.getMemRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER"));

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

    /* 일반회원 정보 수정 페이지 이동 */
    @GetMapping("/mypage")
    public String modifyGeneral(HttpSession session, Model model) {
        // 1. 세션에서 로그인 아이디 확인
        String memId = (String) session.getAttribute("memId");
        if (memId == null) {
            return "redirect:/login";
        }

        // 2. DB에서 현재 회원 정보 조회
        MemberVO member = memberService.getMemberById(memId);
        String loginType = member.getLoginType();

        boolean isFirstInput = (member.getMemGender()==null);

        if(member.getMemNews() ==null){
            member.setMemNews("Y");
        }
//        if(member.getMemGender()==null){
//            member.setMemGender("남");
//        }
        model.addAttribute("member", member);
        model.addAttribute("isFirstInput", isFirstInput);

        // 3. 로그인 타입에 따라 적절한 수정 페이지로 자동 이동
        if (loginType != null) {
            return "/member/modify_nk";
        }
//        else if ("NAVER".equals(loginType)) {
//            return "/member/modifyNaver";
//        }

        // 4. 일반 회원(GENERAL)인 경우에만 화면 실행
        return "member/modify_general";
    }

    /* 일반회원 정보 수정 실행 */
    @PostMapping("mypage/modify")
    public String modifyMember(MemberVO member, RedirectAttributes rttr) {
        // 1. 넘어온 데이터 확인 (디버깅)
        System.out.println("수정 요청 회원 정보: " + member.toString());

        // 2. 서비스 호출
        int result = memberService.modifyMember(member);

        if (result > 0) {
            rttr.addFlashAttribute("msg", "회원정보가 성공적으로 수정되었습니다.");
            return "redirect:/mypage"; // 수정 후 메인이나 마이페이지로 이동
        } else {
            rttr.addFlashAttribute("msg", "정보 수정에 실패하였습니다.");
            return "redirect:/mypage"; // 실패 시 다시 수정 폼으로
        }
    }
}
