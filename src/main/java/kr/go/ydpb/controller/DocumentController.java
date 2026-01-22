package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/document/*")
public class DocumentController {

    @GetMapping("request")
    public String documentRequestForm(HttpSession session) {
        // 로그인 체크는 HTML 내 자바스크립트에서 처리
        return "sub/document_request";
    }
}