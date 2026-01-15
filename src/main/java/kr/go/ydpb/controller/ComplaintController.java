package kr.go.ydpb.controller;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.AdminService;
import kr.go.ydpb.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("admin/complaint")
@AllArgsConstructor
public class ComplaintController {
    //주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintService complaintService;

    //매핑
    @GetMapping("list")
    public String complaintList(Model model, Criteria cri){
        model.addAttribute("complaintList", complaintService.getComplaintWithPaging(cri));
        int total = complaintService.getAllCount(cri);
        model.addAttribute("pageMaker",new PageDTO(cri,total));

        return "admin/admin_complaint_list";
    }

    // 데이터만 가져옴
    @GetMapping("view")
    public String complaintView(@RequestParam("comId") int comId,
                                @ModelAttribute("cri") Criteria cri, Model model){
        model.addAttribute("complaint",complaintService.getOneComplaint(comId));

        return "admin/admin_complaint_view";
    }
    @GetMapping("update")
    public String getComplaintUpdate(@RequestParam("comId") int comId,
                                @ModelAttribute("cri") Criteria cri, Model model){
        model.addAttribute("complaint",complaintService.getOneComplaint(comId));

        return "admin/admin_complaint_update";
    }

    @PostMapping("update")
    public String complaintUpdate(ComplaintVO vo , @ModelAttribute Criteria cri , RedirectAttributes rttr){
        complaintService.updateComplaint(vo);
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("comId", vo.getComId());
        return "redirect:/admin/complaint/view";
    }

    @GetMapping("delete")
    public String complaintDelete(@RequestParam("comId") int comId,
                                  @ModelAttribute ("cri") Criteria cri,
                                  RedirectAttributes rttr) {
        complaintService.deleteComplaint(comId);


        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());

        // 삭제 후 목록


        return "redirect:/admin/complaint/list";
    }
}
