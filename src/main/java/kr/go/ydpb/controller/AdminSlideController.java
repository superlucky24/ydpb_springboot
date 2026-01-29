package kr.go.ydpb.controller;

import kr.go.ydpb.config.FileUploadUtil;
import kr.go.ydpb.domain.MainSlideVO;
import kr.go.ydpb.service.MainSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private MainSlideService slideService;
    @Autowired
    private FileUploadUtil fileUtil;

    private String uploadDir = "upload";

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
                           @RequestParam("slide_file") MultipartFile file,
                           RedirectAttributes rttr) throws Exception {

         // 프로젝트 루트

        String projectRoot = new File("").getAbsolutePath();   // 프로젝트 루트
        String realUploadDir = projectRoot + File.separator + uploadDir;


        String saved = fileUtil.saveFile(file, realUploadDir);
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
    public String edit(
            @RequestParam(value="slideId", required=true) Long slideId,
            MainSlideVO slide,
            @RequestParam(value="slide_file", required=false) MultipartFile file,
            RedirectAttributes rttr) throws Exception {

        // slideId 강제 세팅
        slide.setSlideId(slideId);

        // 1) 기존 데이터 조회 (기존 이미지 경로 얻기 위해)
        MainSlideVO oldSlide = slideService.get(slideId);
        String oldImagePath = oldSlide.getImagePath();

        // 2) 파일 업로드가 있으면 기존 파일 삭제 + 새 파일 저장
        if (file != null && !file.isEmpty()) {

            // 기존 파일 삭제
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                String oldFileName = oldImagePath.replace("/upload/", "");

                String projectRoot = new File("").getAbsolutePath();
                String realUploadDir = projectRoot + File.separator + uploadDir;

                fileUtil.deleteFile(realUploadDir + File.separator + oldFileName);
            }

            // 새 파일 저장
            String projectRoot = new File("").getAbsolutePath();
            String realUploadDir = projectRoot + File.separator + uploadDir;
            String saved = fileUtil.saveFile(file, realUploadDir);
            slide.setImagePath("/upload/" + saved);
        }

        // 3) DB 업데이트
        slideService.update(slide);

        rttr.addFlashAttribute("msg", "수정 완료");
        return "redirect:/admin/slide/list";
    }



    // 삭제
    @GetMapping("/delete")
    public String delete(@RequestParam("slideId") Long slideId, RedirectAttributes rttr) {
        MainSlideVO slide = slideService.get(slideId);

        // 실제 업로드 폴더 절대경로
        String projectRoot = new File("").getAbsolutePath();
        String realUploadDir = projectRoot + File.separator + uploadDir;

        String imagePath = slide.getImagePath();   // "/upload/파일명.jpg"

        if (imagePath != null && !imagePath.isEmpty()) {
            String fileName = imagePath.replace("/upload/", "");   // "파일명.jpg"

            // 파일 삭제 (절대경로로 전달)
            fileUtil.deleteFile(realUploadDir + File.separator + fileName);
        }

        slideService.delete(slideId);
        rttr.addFlashAttribute("msg", "삭제 완료");
        return "redirect:/admin/slide/list";
    }
}

