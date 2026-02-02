package kr.go.ydpb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {
    @GetMapping(value = {"/admin", "/admin/"})
    public String adminHome() {
        return "/admin/admin_index";
    }

}
