package kr.go.ydpb.controller;

import kr.go.ydpb.domain.*;
import kr.go.ydpb.service.DongNewsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
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

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/dongnews/*")
public class AdminDongNewsController {
    @Setter(onMethod_ = @Autowired)
    private DongNewsService service;

    // 목록
    @GetMapping("list")
    public String list(@ModelAttribute("cri") Criteria cri,
                       Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "admin/admin_dongnews_list";
    }

    // 글보기
    @GetMapping("view")
    public String view(@RequestParam("dnewsId") Long dnewsId,
                       @ModelAttribute("cri") Criteria cri,
                       Model model) {
        service.increaseCount(dnewsId);
        model.addAttribute("board", service.getBoard(dnewsId));
        model.addAttribute("prev", service.getPrev(dnewsId, cri));
        model.addAttribute("next", service.getNext(dnewsId, cri));
        return "admin/admin_dongnews_view";
    }

    // 글작성 화면
    @GetMapping("write")
    public String writeFrom(@ModelAttribute("cri") Criteria cri) {
        return "admin/admin_dongnews_write";
    }

    // 글작성 실행
    @PostMapping("write")
    public String write(DongNewsVO board,
                        @ModelAttribute("cri") Criteria cri,
                        @RequestParam(value = "file_1", required = false) MultipartFile file1,
                        @RequestParam(value = "file_2", required = false) MultipartFile file2,
                        @RequestParam(value = "file_text_1", required = false) String fileText1,
                        @RequestParam(value = "file_text_2", required = false) String fileText2,
                        @RequestParam(value = "file_opt_1", required = false) String fileOpt1,
                        @RequestParam(value = "file_opt_2", required = false) String fileOpt2,
                        RedirectAttributes rttr) {

        service.register(board, file1, file2, fileText1, fileText2, fileOpt1, fileOpt2);
        if(board.getDnewsId() > 0) {
            return "redirect:/admin/dongnews/list";
        }
        else {
            rttr.addAttribute("pageNum", cri.getPageNum());
            rttr.addAttribute("amount", cri.getAmount());
            rttr.addAttribute("searchType", cri.getSearchType());
            rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
            rttr.addFlashAttribute("errorMsg", "서버 오류입니다.\\n다시 시도해주세요.");
            return "redirect:/admin/dongnews/write";
        }
    }

    // 글수정 화면
    @GetMapping("update")
    public String updateFrom(@RequestParam("dnewsId") Long dnewsId,
                             @ModelAttribute("cri") Criteria cri,
                             Model model) {
        model.addAttribute("board", service.getBoard(dnewsId));
        return "admin/admin_dongnews_update";
    }

    // 글수정 실행
    @PostMapping("update")
    public String update(@ModelAttribute("board") DongNewsVO board,
                         @ModelAttribute("cri") Criteria cri,
                         @RequestParam(value = "file_1", required = false) MultipartFile file1,
                         @RequestParam(value = "file_2", required = false) MultipartFile file2,
                         @RequestParam(value = "file_text_1", required = false) String fileText1,
                         @RequestParam(value = "file_text_2", required = false) String fileText2,
                         @RequestParam(value = "file_opt_1", required = false) String fileOpt1,
                         @RequestParam(value = "file_opt_2", required = false) String fileOpt2,
                         @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                         RedirectAttributes rttr) {
        service.updateBoard(board, file1, file2, fileText1, fileText2, fileOpt1, fileOpt2, deleteFileIds);
        rttr.addAttribute("dnewsId", board.getDnewsId());
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        return "redirect:/admin/dongnews/view";
    }

    // 글삭제
    @PostMapping("delete")
    public String delete(Long dnewsId,
                         @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttr) {
        int result = service.deleteBoard(dnewsId);
        if(result > 0) {
            rttr.addFlashAttribute("errorMsg", "정상적으로 삭제되었습니다.");
        }
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        return "redirect:/admin/dongnews/list";
    }

    // 파일 다운로드 관련 메서드
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) {

        DongNewsFileVO fileVO = service.getFile(fileId);

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

    // 파일 다운로드
    @GetMapping("/downloadattachment")
    public ResponseEntity<Resource> downloadAttachment(@RequestParam("fileId") Long fileId) {

        DongNewsFileVO fileVO = service.getFile(fileId);

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
