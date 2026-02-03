package kr.go.ydpb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "access-denied";
    }
}
