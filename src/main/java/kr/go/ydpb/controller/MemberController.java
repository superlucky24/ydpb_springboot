package kr.go.ydpb.controller;


import kr.go.ydpb.domain.Criteria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class MemberController {
    @GetMapping("/login")
    public String login(Criteria cri, Model model) {


    }
}
