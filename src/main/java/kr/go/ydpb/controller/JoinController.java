package kr.go.ydpb.controller;

import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.JoinService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("member")
public class JoinController {
    @Setter(onMethod_ = @Autowired)
    private JoinService joinService;

    @GetMapping("join")
    public String join(MemberVO member, Model model){
        model.addAttribute("member",member);
        return "member/join";
    }

    @PostMapping("join")
    public String doJoin(@RequestParam("member") MemberVO member){
        joinService.addMember(member);
        //추후 수정
        return "index";
    }
}
