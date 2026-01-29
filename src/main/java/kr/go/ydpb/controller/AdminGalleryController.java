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

    /* 목록 가져오기  */
    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri)); // service.getList(cri) = 게시글 리스트
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri))); // service.getTotal(cri) = 전체 게시글 수
        return "admin/admin_gallery_list";
    }

    /* 글 작성 페이지로 이동 */
    @GetMapping("/write")
    public String writeForm(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("board", new GalleryVO()); // 빈 GalleryVO를 화면에 전달
        return "admin/admin_gallery_write";
    }

    /* 글 작성 처리 */
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
            /* required = false 로 설정시 파일 없어도 글 등록 처리 가능, rttr = 리다이렉트시 정보전달 */
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

    /* HTTP 응답 전체를 자바 객체로 표현한 것 = ResponseEntity
       HTTP 응답 종류 : [상태코드], [헤더], [본문(body)]
        상태코드 : 202, 404, 500 등
        헤더 : Content-Type, Content-Disposition
        본문 : 실제 데이터
    */

    /* 보기 + 다운로드 기능 (ResponseEntity<Resource> → 상태코드 + 헤더 + 파일 데이터를 직접 제어) */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) {

        GalleryFileVO fileVO = service.getFile(fileId);

        /* 파일 정보 없을 때 */
        if (fileVO == null) {
            return ResponseEntity.notFound().build();
        }

        /* 실제 file(object) 생성 */
        File file = new File(
                fileVO.getUploadPath(),
                fileVO.getUuid() + "_" + fileVO.getFileName() //UUID 사용 이유 : 똑같은 파일 이름 중복 방지
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build(); // DB데이터만 있고 서버파일이 삭제 된경우 방어
        }

        /* 파일/스트림을 추상화한 객체 : ResponseEntity의 body로 사용 가능 */
        Resource resource = new FileSystemResource(file);

        /* 파일명 인코딩 (한글 깨짐 방지) */
        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8);
        /* MIME 타입 설정 (MIME = 파일의 정체성(데이터 종류) ex. jpg, png, json, html.... ) */
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {}

        /* 브라우저가 바로 열 수 있으면 열어줌 >> 이미지 / PDF → 미리보기 */
        String contentDisposition = "inline; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }
    /* 오직 다운로드 기능 (ResponseEntity<Resource> → 상태코드 + 헤더 + 파일 데이터를 직접 제어) */
    @GetMapping("/downloadattachment")
    public ResponseEntity<Resource> downloadAttachment(@RequestParam("fileId") Long fileId) {

        GalleryFileVO fileVO = service.getFile(fileId);

        /* 파일 정보 없을 때 */
        if (fileVO == null) {
            return ResponseEntity.notFound().build();
        }

        /* 실제 file(object) 생성 */
        File file = new File(
                fileVO.getUploadPath(),
                fileVO.getUuid() + "_" + fileVO.getFileName() //UUID 사용 이유 : 똑같은 파일 이름 중복 방지
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build(); // DB데이터만 있고 서버파일이 삭제 된경우 방어
        }

        /* 파일/스트림을 추상화한 객체 : ResponseEntity의 body로 사용 가능 */
        Resource resource = new FileSystemResource(file);

        /* 파일명 인코딩 (한글 깨짐 방지) */
        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // URLEncoder는 공백을 +로 바꿈 >> 브라우저에서 + → 공백 인식 오류 >> %20으로 치환
        /* MIME 타입 설정 */
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {}

        /* 무조건 다운로드 창 열기 */
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }
}
