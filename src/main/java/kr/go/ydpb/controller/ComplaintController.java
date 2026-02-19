package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.ComplaintArchiveService;
import kr.go.ydpb.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
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


    // 목록
    @GetMapping("list")
    public String complaintList(Model model,
                                @ModelAttribute("cri") Criteria cri){
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

    // 글보기
    @GetMapping("view")
    public String complaintView(@ModelAttribute("comId") int comId,
                                @ModelAttribute("cri") Criteria cri,
                                RedirectAttributes rttr,
                                HttpSession session,
                                Model model){
        String loginId = (String)session.getAttribute("memId");
        boolean isAdmin = session.getAttribute("admin") != null && (Integer)session.getAttribute("admin") == 1;
        ComplaintVO complaint = complaintService.getOneComplaint(comId);
        // 비공개 and 타인이 작성한 글 and 관리자 아님 => 목록으로 돌려보내기
        if(complaint.getComPublic() == 0 && !complaint.getMemId().equals(loginId) && !isAdmin) {
            rttr.addFlashAttribute("errorMsg", "권한이 없습니다.");
            return "redirect:/complaint/list";
        }
        else {
            model.addAttribute("complaint", complaint);
            return "sub/complaint_view";
        }
    }

    // 글작성 화면
    @GetMapping("write")
    public String complaintWriteFrom(@ModelAttribute("cri") Criteria cri,
                                     HttpSession session) {
        // 비로그인 상태에서 강제로 접속했을 때, 로그인 화면으로 이동
        if(session.getAttribute("memId") == null) {
            return "redirect:/login";
        }
        else {
            return "sub/complaint_write";
        }
    }

    // 글작성 실행
    @PostMapping("write")
    public String complaintWrite(ComplaintVO cvo,
                                 @ModelAttribute("cri") Criteria cri,
                                 RedirectAttributes rttr) {
        int result = complaintService.insertComplaint(cvo);


        // 글작성 성공 시 목록 화면으로 이동
        if(result == 1) {
            return "redirect:/complaint/list";
        }
        // 글작성 실패 시 페이징 정보 가지고 글쓰기 폼 화면으로 이동
        else {
            rttr.addAttribute("pageNum", cri.getPageNum());
            rttr.addAttribute("amount", cri.getAmount());
            rttr.addAttribute("searchType", cri.getSearchType());
            rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
            rttr.addFlashAttribute("errorMsg", "서버 오류입니다.\\n다시 시도해주세요.");
            return "redirect:/complaint/write";
        }
    }

    // 글수정 화면
    @GetMapping("update")
    public String complaintUpdateFrom(@ModelAttribute("comId") int comId,
                                      @ModelAttribute("cri") Criteria cri,
                                      RedirectAttributes rttr,
                                      HttpSession session,
                                      Model model) {
        String loginId = (String)session.getAttribute("memId");
        boolean isAdmin = session.getAttribute("admin") != null && (Integer)session.getAttribute("admin") == 1;
        ComplaintVO complaint = complaintService.getOneComplaint(comId);

        // 타인이 작성한 글 or 관리자 아님 => 목록으로 돌려보내기
        if(!complaint.getMemId().equals(loginId) && !isAdmin) {
            rttr.addFlashAttribute("errorMsg", "권한이 없습니다.");
            return "redirect:/complaint/list";
        }
        else {
            model.addAttribute("complaint", complaint);
            return "sub/complaint_update";
        }
    }

    // 글수정 실행
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

    // 글삭제
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
        return "redirect:/complaint/list";
    }

}
