package kr.go.ydpb.controller;

import kr.go.ydpb.domain.CommunityFileVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.CommunityService;
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
@RequestMapping("/community/*")
public class CommunityController {
    @Autowired
    private CommunityService service;

    /* 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/community_center_list";
    }

    /* 상세 보기 */
    @GetMapping("/view")
    public String view(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri, Model model) {
        // 조회수 증가
        service.increaseCount(cmntId);

        // 필요한 데이터 받음(board, prev, next)
        model.addAttribute("board", service.get(cmntId));
        model.addAttribute("prev", service.getPrev(cmntId, cri));
        model.addAttribute("next", service.getNext(cmntId, cri));
        return "sub/community_center_view";
    }

    // 파일 보기 + 다운로드
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) {

        CommunityFileVO fileVO = service.getFile(fileId);

        // 파일 정보 없을 때
        if (fileVO == null) {
            return ResponseEntity.notFound().build();
        }

        // 실제 file(object) 생성
        File file = new File(
                fileVO.getUploadPath(),
                fileVO.getUuid() + "_" + fileVO.getFileName()
        );

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 파일/스트림을 추상화한 객체 : ResponseEntity의 body로 사용 가능
        Resource resource = new FileSystemResource(file);

        // 파일명 인코딩 (한글 깨짐 방지)
        String encodedFileName = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8);

        // 확장자에 따라 MIME 타입 자동 지정 (MIME = 파일의 정체성(데이터 종류) ex. jpg, png, json, html.... )
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        } catch (Exception e) {
            // 무시
        }

        // 브라우저가 바로 열 수 있으면 열어줌 >> 이미지 / PDF → 미리보기
        String contentDisposition = "inline; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }

    // 파일 다운로드
    @GetMapping("/downloadattachment")
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

        // URLEncoder는 공백을 +로 바꿈 >> 브라우저에서 + → 공백 인식 오류 >> %20으로 치환
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

        // 무조건 다운로드 창 열기 : 여기만 inline -> attachment로 변경
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(resource);
    }
}
