package kr.go.ydpb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sub")
public class StatusController {

    @GetMapping("/status")
    public String status(){
        return "sub/status";
    }
}
