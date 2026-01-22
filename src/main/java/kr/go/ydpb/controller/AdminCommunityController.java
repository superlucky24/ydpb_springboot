package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.CommunityFileVO;
import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/community")
public class AdminCommunityController {

    private final CommunityService service;

    /* =========================
       목록
     ========================= */
    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "admin/admin_community_center_list";
    }

    /* =========================
       글쓰기 페이지
     ========================= */
    @GetMapping("/write")
    public String writeForm(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("board", new CommunityVO());
        return "admin/admin_community_center_write";
    }

    /* =========================
       글 등록
     ========================= */
    @PostMapping("/write")
    public String register(
            CommunityVO board,
            @RequestParam(value = "file_1", required = false) MultipartFile file1,
            @RequestParam(value = "file_2", required = false) MultipartFile file2,
            @RequestParam(value = "file_text_1", required = false) String fileText1,
            @RequestParam(value = "file_text_2", required = false) String fileText2,
            @RequestParam(value = "file_opt_1", required = false) String fileOpt1,
            @RequestParam(value = "file_opt_2", required = false) String fileOpt2,
            RedirectAttributes rttr,
            HttpSession session
    ) {

        // 세션에서 memId 가져오기
        String memId = (String) session.getAttribute("memId");
        board.setMemId(memId);

        service.register(board, file1, file2, fileText1, fileText2, fileOpt1, fileOpt2);
        rttr.addFlashAttribute("result", board.getCmntId());

        return "redirect:/admin/community/list";
    }


    /* =========================
       상세보기
     ========================= */
    @GetMapping("/view")
    public String view(
            @RequestParam("cmntId") Long cmntId,
            @ModelAttribute("cri") Criteria cri,
            Model model
    ) {
        service.increaseCount(cmntId);
        model.addAttribute("board", service.get(cmntId));
        return "admin/admin_community_center_view";
    }

    /* =========================
       수정 페이지
     ========================= */
    @GetMapping("/update")
    public String updateForm(
            @RequestParam("cmntId") Long cmntId,
            @ModelAttribute("cri") Criteria cri,
            Model model
    ) {
        model.addAttribute("board", service.get(cmntId));
        return "admin/admin_community_center_update";
    }

    /* =========================
       수정 처리
     ========================= */
    @PostMapping("/update")
    public String update(
            CommunityVO board,
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



        // 1. 기존 게시글 조회
        CommunityVO existing = service.get(board.getCmntId());

        // 2. 기존 memId 유지
        board.setMemId(existing.getMemId());

        service.modify(
                board,
                file1,
                file2,
                fileText1,
                fileText2,
                fileOpt1,
                fileOpt2,
                deleteFileIds
        );

        rttr.addFlashAttribute("result", "success");
        rttr.addAttribute("cmntId", board.getCmntId());
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/community/view";
    }

    /* =========================
       삭제 처리
     ========================= */
    @PostMapping("/delete")
    public String delete(
            @RequestParam("cmntId") Long cmntId,
            @ModelAttribute("cri") Criteria cri,
            RedirectAttributes rttr
    ) {

        if (service.remove(cmntId)) {
            rttr.addFlashAttribute("result", "success");
        }

        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/community/list";
    }

    /*이미지 추가 관련 메서드 */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) {

        CommunityFileVO fileVO = service.getFile(fileId); // 1) 서비스에서 파일 조회

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

        // 파일명 인코딩
        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8);

        // 확장자에 따라 MIME 타입 자동 지정
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {
            // 무시
        }

        String contentDisposition = "inline; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }

    /*이미지 다운로드 */
    @GetMapping("/downloadAttachment")
    public ResponseEntity<Resource> downloadAttachment(@RequestParam("fileId") Long fileId) {

        CommunityFileVO fileVO = service.getFile(fileId);

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
        } catch (Exception e) {
            // 무시
        }

        // 여기만 inline -> attachment로 변경
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }
}
