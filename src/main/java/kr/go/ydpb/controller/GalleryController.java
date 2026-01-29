package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GalleryFileVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.GalleryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
@AllArgsConstructor
@RequestMapping("gallery/*")
public class GalleryController {

    @Autowired
    private GalleryService service;

    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/gallery_list";
    }


    @GetMapping("/view")
    public String view(
            @RequestParam("galId") Long galId,
            @ModelAttribute("cri") Criteria cri,
            Model model
    ) {
        service.increaseCount(galId);
        model.addAttribute("board", service.get(galId));
        // 이전글 / 다음글 추가
        model.addAttribute("prev", service.getPrev(galId, cri));
        model.addAttribute("next", service.getNext(galId, cri));

        return "sub/gallery_view";
    }

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
                fileVO.getUuid() + "_" + fileVO.getFileName()
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
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
                fileVO.getUuid() + "_" + fileVO.getFileName()
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        /* 파일/스트림을 추상화한 객체 : ResponseEntity의 body로 사용 가능 */
        Resource resource = new FileSystemResource(file);
        /* 파일명 인코딩 (한글 깨짐 방지) */
        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
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
