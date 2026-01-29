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

// 회원가입 처리 컨트롤러 - 귀환+상림
@Controller
@AllArgsConstructor
@RequestMapping("member")
public class JoinController {
    // 회원가입 서비스 주입
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

// 회원가입 요청 처리
    @PostMapping("join")
    // 회원가입 정보 MemberVO, BindingResult - 자바에서 유효성 검사 결과
    public String doJoin(@Valid MemberVO member ,
                         BindingResult bindingResult,
                         RedirectAttributes rttr,
                         HttpSession session,
                         @RequestParam("memPasswordRe") String memPasswordRe){

        // 아이디 중복 확인 메서드 실행
        boolean idExist = joinService.isIdExist(member.getMemId());
        // 생년월일이 오늘 이후인 경우 예외처리
        if (member.getMemBirth() != null &&
            member.getMemBirth().isAfter(LocalDate.now())) {
            //메시지 바인딩하여 리다이렉트
            rttr.addFlashAttribute("msg", "생년월일은 오늘 이후일 수 없습니다.");
            return "redirect:/member/joinstep3";
        }
        // 아이디가 중복되지 않고 비밀번호 일치 + 유효성 검사 에러 없는 경우
        if(!idExist && member.getMemPassword().equals(memPasswordRe) && !bindingResult.hasErrors()){ 
            // 회원 등록 메서드 실행
            joinService.addMember(member);
            // 회원가입 성공 메시지와 회원 정보 1회성 바인딩
            rttr.addFlashAttribute("msg",member.getMemName()+"님 회원가입 성공!! 로그인해주세요");
            rttr.addFlashAttribute("member",member);

            // 인증을 위해 저장한 세션값 삭제
            session.removeAttribute("memName");
            session.removeAttribute("memBirth");
            session.removeAttribute("memGender");
            session.removeAttribute("memPhone");
            //메인페이지 실행
            return "redirect:/";
        }
        else{// 중복 아이디 존재하거나 비밀번호 불일치, 유효성 검사 실패 시
            rttr.addFlashAttribute( "msg","회원가입 실패 : 다시 가입해주세요");
            return "redirect:/member/joinstep3";
        }
        //추후 수정
    }

    // 아이디 중복 체크
    @ResponseBody //json 응답으로 변경
    @GetMapping("checkid")
    // 키 문자열, 밸류 boolean 타입 Map으로 리턴, 입력한 회원 아이디로 체크
    public Map<String, Boolean> checkId(@RequestParam String memId) {
        // 중복 체크 메서드 실행
        boolean available = !joinService.isIdExist(memId); // 존재하면 false
        // 요소가 딱 하나만 들어있는 읽기 전용 Map 리턴
        // 브라우저는 최종적으로 {"available": true} 또는 {"available": false} 형태의 JSON 데이터를 받게됨
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
