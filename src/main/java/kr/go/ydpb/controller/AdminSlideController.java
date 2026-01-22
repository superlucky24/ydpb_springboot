package kr.go.ydpb.controller;

import kr.go.ydpb.config.FileUploadUtil;
import kr.go.ydpb.domain.MainSlideVO;
import kr.go.ydpb.service.MainSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.UUID;

@Controller
@RequestMapping("/admin/slide")
@RequiredArgsConstructor
public class AdminSlideController {

    private MainSlideService slideService;
    private FileUploadUtil fileUtil;

    private String uploadDir = "./upload";

    // 목록
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("list", slideService.getList());
        return "admin/admin_slide_list";
    }

    // 등록 폼
    @GetMapping("/register")
    public String registerForm() {
        return "admin/admin_slide_register";
    }

    // 등록 처리
    @PostMapping("/register")
    public String register(MainSlideVO slide,
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes rttr) throws Exception {

        String saved = fileUtil.saveFile(file, uploadDir);
        slide.setImagePath("/upload/" + saved);

        slideService.insert(slide);
        rttr.addFlashAttribute("msg", "등록 완료");
        return "redirect:/admin/slide/list";
    }

    // 수정 폼
    @GetMapping("/edit")
    public String editForm(@RequestParam("slideId") Long slideId, Model model) {
        model.addAttribute("slide", slideService.get(slideId));
        return "admin/admin_slide_edit";
    }

    // 수정 처리
    @PostMapping("/edit")
    public String edit(MainSlideVO slide,
                       @RequestParam("file") MultipartFile file,
                       RedirectAttributes rttr) throws Exception {

        if(!file.isEmpty()) {
            String saved = fileUtil.saveFile(file, uploadDir);
            slide.setImagePath("/upload/" + saved);
        }

        slideService.update(slide);
        rttr.addFlashAttribute("msg", "수정 완료");
        return "redirect:/admin/slide/list";
    }

    // 삭제
    @GetMapping("/delete")
    public String delete(@RequestParam("slideId") Long slideId, RedirectAttributes rttr) {
        MainSlideVO slide = slideService.get(slideId);

        // 파일 삭제
        fileUtil.deleteFile("./upload/" + slide.getImagePath().substring(8)); // "/upload/" 제거

        slideService.delete(slideId);
        rttr.addFlashAttribute("msg", "삭제 완료");
        return "redirect:/admin/slide/list";
    }
}

