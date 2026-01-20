package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AdminPageAdvice {

    @ModelAttribute
    public void addAdminPageFlag(HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/admin")) {
            model.addAttribute("isAdminPage", true);
        }
    }
}
