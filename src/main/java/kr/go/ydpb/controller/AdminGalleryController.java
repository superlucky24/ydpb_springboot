package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryFileVO;
import kr.go.ydpb.domain.GalleryVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.GalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/gallery")
public class AdminGalleryController {

    @Autowired
    private GalleryService service;

    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "admin/admin_gallery_list";
    }

    @GetMapping("/write")
    public String writeForm(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("board", new GalleryVO());
        return "admin/admin_gallery_write";
    }

    @PostMapping("/write")
    public String register(
            GalleryVO board,
            @RequestParam(value = "file_1", required = false) MultipartFile file1,
            @RequestParam(value = "file_2", required = false) MultipartFile file2,
            @RequestParam(value = "file_text_1", required = false) String fileText1,
            @RequestParam(value = "file_text_2", required = false) String fileText2,
            @RequestParam(value = "file_opt_1", required = false) String fileOpt1,
            @RequestParam(value = "file_opt_2", required = false) String fileOpt2,
            RedirectAttributes rttr,
            HttpSession session
    ) {

        String memId = (String) session.getAttribute("memId");
        board.setMemId(memId);

        service.register(board, file1, file2, fileText1, fileText2, fileOpt1, fileOpt2);
        rttr.addFlashAttribute("result", board.getGalId());

        return "redirect:/admin/gallery/list";
    }

    @GetMapping("/view")
    public String view(
            @RequestParam("galId") Long galId,
            @ModelAttribute("cri") Criteria cri,
            Model model
    ) {
        service.increaseCount(galId);
        model.addAttribute("board", service.get(galId));
        return "admin/admin_gallery_view";
    }

    @GetMapping("/update")
    public String updateForm(
            @RequestParam("galId") Long galId,
            @ModelAttribute("cri") Criteria cri,
            Model model
    ) {
        model.addAttribute("board", service.get(galId));
        return "admin/admin_gallery_update";
    }

    @PostMapping("/update")
    public String update(
            GalleryVO board,
            @RequestParam(value = "file_1", required = false) MultipartFile file1,
            @RequestParam(value = "file_2", required = false) MultipartFile file2,
            @RequestParam(value = "file_text_1", required = false) String fileText1,
            @RequestParam(value = "file_text_2", required = false) String fileText2,
            @RequestParam(value = "file_opt_1", required = false) String fileOpt1,
            @RequestParam(value = "file_opt_2", required = false) String fileOpt2,
            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
            @ModelAttribute("cri") Criteria cri,
            RedirectAttributes rttr
    ) {

        GalleryVO existing = service.get(board.getGalId());
        board.setMemId(existing.getMemId());

        service.modify(board, file1, file2, fileText1, fileText2, fileOpt1, fileOpt2, deleteFileIds);

        rttr.addFlashAttribute("result", "success");
        rttr.addAttribute("galId", board.getGalId());
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/gallery/view";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam("galId") Long galId,
            @ModelAttribute("cri") Criteria cri,
            RedirectAttributes rttr
    ) {

        if (service.remove(galId)) {
            rttr.addFlashAttribute("result", "success");
        }

        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/gallery/list";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) {

        GalleryFileVO fileVO = service.getFile(fileId);

        if (fileVO == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(
                fileVO.getUploadPath(),
                fileVO.getUuid() + "_" + fileVO.getFileName()
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {}

        String contentDisposition = "inline; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/downloadAttachment")
    public ResponseEntity<Resource> downloadAttachment(@RequestParam("fileId") Long fileId) {

        GalleryFileVO fileVO = service.getFile(fileId);

        if (fileVO == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(
                fileVO.getUploadPath(),
                fileVO.getUuid() + "_" + fileVO.getFileName()
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {}

        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }
}
