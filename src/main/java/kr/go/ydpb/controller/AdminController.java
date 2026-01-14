package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.mapper.AdminMapper;
import kr.go.ydpb.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
@AllArgsConstructor
public class AdminController {
    //주입
    @Setter(onMethod_ = @Autowired)
    private AdminService adminService;

    //매핑
    @GetMapping("complaint/list")
    public String complaintList(Model model, Criteria cri){
        model.addAttribute("complaintList", adminService.getComplaintWithPaging(cri));
        int total = adminService.getAllCount(cri);
        model.addAttribute("pageMaker",new PageDTO(cri,total));

        return "admin/admin_complaint_list";
    }
}
