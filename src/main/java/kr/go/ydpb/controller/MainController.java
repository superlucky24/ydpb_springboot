package kr.go.ydpb.controller;

import kr.go.ydpb.service.MainSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private MainSlideService slideService;

    @GetMapping("/")
    public String index(Model model){

        model.addAttribute("slides", slideService.getList());
        return "index";
    }
}
