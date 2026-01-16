package kr.go.ydpb.controller;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
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
    public String complaintList(Model model, Criteria cri){
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
    public String complaintView(@RequestParam("comId") int comId, @ModelAttribute("cri") Criteria cri, Model model, RedirectAttributes rttr){
        model.addAttribute("complaint", complaintService.getOneComplaint(comId));

        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        rttr.addAttribute("searchType", cri.getSearchType());

        return "sub/complaint_view";
    }

}
