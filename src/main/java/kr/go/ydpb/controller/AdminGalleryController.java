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
@RequestMapping("admin/gallery/*")
public class AdminGalleryController {

    @GetMapping("list")
    public String galleryList(@ModelAttribute("cri") Criteria cri, Model model){
        return "admin/admin_gallery_list";
    }


    @GetMapping("view")
    public String galleryView(){
        return "admin/admin_gallery_view";
    }


    @GetMapping("write")
    public String galleryWriteForm(){
        return "admin/admin_gallery_write";
    }

    @PostMapping("write")
    public String galleryWrite(){
        return "redirect:/admin/gallery/list";
    }

    @GetMapping("update")
    public String galleryUpdateForm(){
        return "admin/admin_gallery_view";
    }

    @PostMapping("update")
    public String galleryUpdate(){
        return "admin/galley/view";
    }

    @PostMapping("delete")
    public String galleryDelete(){
        return "redirect:/admin/gallery/list";
    }


}
