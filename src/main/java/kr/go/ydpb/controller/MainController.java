package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.service.GalleryService;
import kr.go.ydpb.service.MainSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private MainSlideService slideService;
    @Autowired
    private GalleryService service;

    @GetMapping("/")
    public String index(@ModelAttribute("cri") Criteria cri, Model model){
        
        model.addAttribute("gallery_list", service.getList(cri)); // 갤러리 값
        model.addAttribute("slides", slideService.getMainList()); // 슬라이드 값
        return "index";
    }
}
