package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("gallery/*")
public class GalleryController {
    @GetMapping("list")
    public String galleryList(@ModelAttribute("cri") Criteria cri, Model model){
        return "sub/gallery_list";
    }


    @GetMapping("view")
    public String galleryView(){
        return "sub/gallery_view";
    }

}
