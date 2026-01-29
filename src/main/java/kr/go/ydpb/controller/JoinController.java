package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.JoinService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.Collections;
import java.util.Map;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("member")
public class JoinController {
    @Setter(onMethod_ = @Autowired)
    private JoinService joinService;

    @GetMapping("joinstep3")
    public String join(@ModelAttribute("member") MemberVO member, HttpSession session){
        // 새로고침해도 인증받은 값은 유지되도록 세션값으로 받음
        member.setMemName((String)session.getAttribute("memName"));
        member.setMemBirth((LocalDate) session.getAttribute("memBirth"));
        member.setMemGender((String)session.getAttribute("memGender"));
        member.setMemPhone((String)session.getAttribute("memPhone"));
        member.setMemNews("Y");
        return "member/join";
    }

    @PostMapping("join")
    public String doJoin(@Valid MemberVO member ,
                         BindingResult bindingResult,
                         RedirectAttributes rttr,
                         HttpSession session,
                         @RequestParam("memPasswordRe") String memPasswordRe){

        boolean idExist = joinService.isIdExist(member.getMemId());
        if (member.getMemBirth() != null &&
            member.getMemBirth().isAfter(LocalDate.now())) {

            rttr.addFlashAttribute("msg", "생년월일은 오늘 이후일 수 없습니다.");
            return "redirect:/member/joinstep3";
        }
        if(!idExist && member.getMemPassword().equals(memPasswordRe) && !bindingResult.hasErrors()){ // 아이디가 중복되지 않고 비밀번호 일치 시

            joinService.addMember(member);
            rttr.addFlashAttribute("msg",member.getMemName()+"님 회원가입 성공!! 로그인해주세요");
            rttr.addFlashAttribute("member",member);

            // 인증을 위해 저장한 세션값 삭제
            session.removeAttribute("memName");
            session.removeAttribute("memBirth");
            session.removeAttribute("memGender");
            session.removeAttribute("memPhone");

            return "redirect:/";
        }
        else{// 중복 아이디 존재하거나 비밀번호 불일치
            rttr.addFlashAttribute( "msg","회원가입 실패 : 다시 가입해주세요");
            return "redirect:/member/joinstep3";
        }
        //추후 수정
    }

    // 아이디 중복 체크
    @ResponseBody //json 응답으로 변경
    @GetMapping("checkId")
    public Map<String, Boolean> checkId(@RequestParam String memId) {
        boolean available = !joinService.isIdExist(memId); // 존재하면 false
        return Collections.singletonMap("available", available);
    }

    // 회원가입 1단계
    @GetMapping("/join")
    public String joinStep1() {
        return "member/join_step1";
    }

    // 인증 팝업
    @GetMapping("/authform")
    public String authForm() {
        return "member/auth_form";
    }

    // 회원가입 2단계
    @PostMapping("/joinstep2")
    public String joinStep2(@ModelAttribute("member") MemberVO member, HttpSession session) {
        // 인증 시 전달받은 값 세션에 저장
        session.setAttribute("memName", member.getMemName());
        session.setAttribute("memBirth", member.getMemBirth());
        session.setAttribute("memGender", member.getMemGender());
        session.setAttribute("memPhone", member.getMemPhone());
        return "redirect:/member/joinstep3";
    }
}
