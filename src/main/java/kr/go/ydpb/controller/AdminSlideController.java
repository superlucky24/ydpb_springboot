package kr.go.ydpb.controller;

import kr.go.ydpb.domain.MainSlideVO;
import kr.go.ydpb.service.MainSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

    private final MainSlideService slideService;

    @GetMapping("/register")
    public String registerForm() {
        return "admin/admin_slide_register";
    }

    @PostMapping("/register")
    public String register(
            MainSlideVO slide,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes rttr
    ) throws Exception {

        // 1) 파일 업로드
        String uploadDir = "./upload/";
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        File dest = new File(uploadDir + savedFilename);
        file.transferTo(dest);

        // 2) DB 저장
        slide.setImagePath("/upload/" + savedFilename);
        slideService.insertSlide(slide);

        rttr.addFlashAttribute("msg", "슬라이드 등록 성공");
        return "redirect:/admin/slide/register";
    }
}

