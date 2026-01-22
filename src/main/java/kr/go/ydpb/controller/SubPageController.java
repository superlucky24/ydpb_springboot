package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.DocumentDTO;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/sub")
public class SubPageController {

    // 가족관계증명서 신청폼 데이터를 받기 위함
    private final MemberService memberService;

    @GetMapping("/status")
    public String status(){
        return "sub/status";
    }

    @GetMapping("/location")
    public String location() {

        return "sub/location";
    }

    @GetMapping ("/complaint_sector")
    public String complaintSector(){

        return "sub/complaint_sector";
    }

    @GetMapping("/hunjang")
    public String hunjang(){ return "sub/hunjang";}

    @GetMapping("/document")
    public String documentRequest(HttpSession session, Model model) {
        String memId = (String) session.getAttribute("memId");

        if (memId == null) {
            return "sub/document_request";
        } else {
            MemberVO member = memberService.getMemberById(memId);
            model.addAttribute("member", member);
            return "sub/document_request";
        }
    }

    @PostMapping("/complete")
    public String processComplete(DocumentDTO docDto, HttpSession session) {
        session.setAttribute("tempDoc", docDto);
        return "redirect:/sub/complete";
    }

    @GetMapping("/complete")
    public String showComplete(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");

        if (docDto == null) {
            return "redirect:/sub/document";
        }

        model.addAttribute("doc", docDto);
        return "sub/document_complete";
    }

    @GetMapping("/print")
    public String showPrint(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");
        model.addAttribute("doc", docDto);

        if (docDto == null) {
            return "redirect:/sub/document";
        }

        return "sub/document_print";
    }
}
