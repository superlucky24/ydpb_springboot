package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/complaint/*")
public class ComplaintController {
    @Setter(onMethod_ = @Autowired)
    private ComplaintService complaintService;

    @GetMapping("list")
    public String complaintList(Model model, @ModelAttribute("cri") Criteria cri){
        List<ComplaintVO> complaintList = complaintService.getComplaintWithPaging(cri);
        if(complaintList==null){
            complaintList= new ArrayList<>();
        }
        model.addAttribute("complaintList", complaintList);
        int total = complaintService.getAllCount(cri);
        if(cri.getSearchType()!=null){
            total = complaintService.getAllSearchCount(cri);
        }
        model.addAttribute("pageMaker",new PageDTO(cri,total));

        return "sub/complaint_list";
    }

    @GetMapping("view")
    public String complaintView(@ModelAttribute("comId") int comId,
                                @ModelAttribute("cri") Criteria cri,
                                RedirectAttributes rttr,
                                HttpSession session,
                                Model model){
        String loginId = (String)session.getAttribute("memId");
        ComplaintVO complaint = complaintService.getOneComplaint(comId);
        // 비공개 + 본인이 작성한 글이 아닐 시 목록으로 돌려보내기
        if(complaint.getComPublic() == 0 && !complaint.getMemId().equals(loginId)) {
            rttr.addFlashAttribute("errorMsg", "권한이 없습니다.");
            return "redirect:/complaint/list";
        }
        else {
            model.addAttribute("complaint", complaint);
            return "sub/complaint_view";
        }
    }

    @GetMapping("write")
    public String complaintWriteFrom(@ModelAttribute("cri") Criteria cri, HttpSession session,
                                     @AuthenticationPrincipal Object principal) {
        if(session.getAttribute("memId") == null && principal ==null) {
            return "redirect:/complaint/list";
        }
        else {
            return "sub/complaint_write";
        }
    }

    @PostMapping("write")
    public String complaintWrite(ComplaintVO cvo, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        int result = complaintService.insertComplaint(cvo);
        if(result == 1) {
            return "redirect:/complaint/list";
        }
        else {
            rttr.addFlashAttribute("pageNum", cri.getPageNum());
            rttr.addFlashAttribute("amount", cri.getAmount());
            rttr.addFlashAttribute("searchType", cri.getSearchType());
            rttr.addFlashAttribute("searchKeyword", cri.getSearchKeyword());
            rttr.addFlashAttribute("errorMsg", "서버 오류입니다.\\n다시 시도해주세요.");
            return "redirect:/complaint/write";
        }
    }

    @GetMapping("update")
    public String complaintUpdateFrom(@ModelAttribute("comId") int comId, @ModelAttribute("cri") Criteria cri, Model model, HttpSession session, RedirectAttributes rttr) {
        String loginId = (String)session.getAttribute("memId");
        if(loginId == null || loginId.isEmpty()) {
            rttr.addFlashAttribute("errorMsg", "권한이 없습니다.");
            return "redirect:/complaint/list";
        }
        else {
            model.addAttribute("complaint", complaintService.getOneComplaint(comId));
            return "sub/complaint_update";
        }
    }

    @PostMapping("update")
    public String complaintUpdate(@ModelAttribute("complaint") ComplaintVO complaint, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        complaintService.updateComplaintUser(complaint);
        rttr.addAttribute("comId", complaint.getComId());
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        return "redirect:/complaint/view";
    }

    @PostMapping("delete")
    public String complaintDelete(int comId, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        int result = complaintService.deleteComplaint(comId);
        if(result > 0) {
            rttr.addFlashAttribute("errorMsg", "정상적으로 삭제되었습니다.");
        }
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        return "redirect:/admin/dongnews/list";
    }

}
