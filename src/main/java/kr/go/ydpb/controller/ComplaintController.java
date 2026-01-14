package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.AdminService;
import kr.go.ydpb.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin")
@AllArgsConstructor
public class ComplaintController {
    //주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintService complaintService;

    //매핑
    @GetMapping("complaint/list")
    public String complaintList(Model model, Criteria cri){
        model.addAttribute("complaintList", complaintService.getComplaintWithPaging(cri));
        int total = complaintService.getAllCount(cri);
        model.addAttribute("pageMaker",new PageDTO(cri,total));

        return "admin/admin_complaint_list";
    }


    @GetMapping("complaint/view")
    public String complaintView(@RequestParam("comId") int comId,
                                @ModelAttribute("cri") Criteria cri, Model model){
        model.addAttribute("complaint",complaintService.getOneComplaint(comId));

        return "admin/admin_complaint_view";
    }
}
