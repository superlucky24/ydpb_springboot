package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.DocumentDTO;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/* 결제 및 문서 출력 페이지 컨트롤러 */
@Controller
@RequestMapping("/document")
public class DocumentController {

    // 가족관계증명서 신청폼 데이터를 받기 위함
    private final MemberService memberService;


    @Value("${portone.store.id}")
    private String storeId;

    @Value("${portone.channel.key}")
    private String channelKey;

    public DocumentController(MemberService memberService) {
        this.memberService = memberService;
    }

    // /있을때 없을때 구분되어 둘다 처리, 기본경로 접속시 신청페이지로 연결
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/document/request";
    }

    /* 1. 문서 신청 폼 화면 */
    @GetMapping("/request")
    public String documentRequest(HttpSession session, Model model) {
        String memId = (String) session.getAttribute("memId");

        if (memId == null) {
            // 서버체크 : 비로그인시 로그인창으로
            return "redirect:/login";
        } else {
            MemberVO member = memberService.getMemberById(memId);
            model.addAttribute("member", member);

            model.addAttribute("portoneStoreId", storeId);
            model.addAttribute("portoneChannelKey", channelKey);

            return "sub/document_request";
        }
    }
    /* 2. 신청 데이터 세션 저장 */
    @PostMapping("/complete")
    public String processComplete(DocumentDTO docDto, HttpSession session) {
        // 세션에 임시 저장
        session.setAttribute("tempDoc", docDto);
        // 새로고침시 중복 제출 방지
        return "redirect:/document/complete";
    }

    /* 3. 신청 완료 및 데이터 확인 화면 */
    @GetMapping("/complete")
    public String showComplete(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");

        // 비정상적인 접근 방지
        if (docDto == null) {
            // 다시 신청 페이지로
            return "redirect:/document/request";
        }

        model.addAttribute("doc", docDto);
        return "sub/document_complete";
    }

    /* 4. 문서 출력용 화면 */
    @GetMapping("/print")
    public String showPrint(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");

        // 보안 체크
        if (docDto == null) {
            return "redirect:/document/request";
        }

        model.addAttribute("doc", docDto);
        return "sub/document_print";
    }
}