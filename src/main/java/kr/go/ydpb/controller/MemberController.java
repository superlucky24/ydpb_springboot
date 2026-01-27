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
import org.springframework.beans.factory.annotation.Autowired;
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

    /* 정보 수정 페이지 통합 입구 */
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        // 1. 로그인 체크
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
        } else {
            rttr.addFlashAttribute("msg", "정보 수정에 실패하였습니다.");
        }
        return "redirect:/mypage";
    }


    /* 일반회원 비밀번호 변경     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 암호화 도구 주입

    // 1. 현재 비밀번호 확인 페이지 이동
    @GetMapping("/confirmPassword")
    public String confirmPasswordPage() {
        return "/member/confirm_password";
    }

    // 2. 현재 비밀번호 검증 처리
    @PostMapping("/verifyPassword")
    public String verifyPassword(@RequestParam("currentPassword") String currentPassword, HttpSession session, RedirectAttributes rttr) {

        String memId = (String) session.getAttribute("memId");
        MemberVO member = memberService.getMemberById(memId);

        // matches(입력한 평문, DB의 암호화된 값) 비교
        if (member != null && passwordEncoder.matches(currentPassword, member.getMemPassword())) {
            session.setAttribute("passwordVerified", true);
            return "redirect:/changePassword";
        } else {
            rttr.addFlashAttribute("msg", "비밀번호가 일치하지 않습니다.");
            return "redirect:/confirmPassword";
        }
    }

    // 3. 새 비밀번호 입력 페이지 이동
    @GetMapping("/changePassword")
    public String changePasswordPage(HttpSession session) {
        // 인증을 거치지 않고 주소창으로 바로 접근하는 것을 방지
        Boolean isVerified = (Boolean) session.getAttribute("passwordVerified");
        if (isVerified == null || !isVerified) {
            return "redirect:/confirmPassword";
        }

        // 확인 후 바로 삭제
        session.removeAttribute("passwordVerified");

        return "/member/change_password";
    }

    // 비밀번호 최종 업데이트
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("newPassword") String newPassword, HttpSession session, RedirectAttributes rttr) {

        String memId = (String) session.getAttribute("memId");

        int result = memberService.updatePassword(memId, newPassword);

        if (result > 0) {
            // 성공 시 인증 세션 제거 및 알림
            session.removeAttribute("passwordVerified");
            rttr.addFlashAttribute("msg", "비밀번호 변경이 완료되었습니다.");
            return "redirect:/mypage";
        } else {
            rttr.addFlashAttribute("msg", "비밀번호 변경에 실패했습니다.");
            return "redirect:/changePassword";
        }
    }

    @ResponseBody
    @PostMapping("/checkPasswordDuplicate")
    public boolean checkPasswordDuplicate(@RequestParam("newPassword") String newPassword, HttpSession session) {
        String memId = (String) session.getAttribute("memId");
        MemberVO member = memberService.getMemberById(memId);

        // 기존 비밀번호와 일치하면 true, 다르면 false 리턴
        return passwordEncoder.matches(newPassword, member.getMemPassword());
    }
}
