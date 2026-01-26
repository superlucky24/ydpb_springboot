package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.DongnewsVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.DongnewsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/dongnews/*")
public class AdminDongnewsController {
    @Setter(onMethod_ = @Autowired)
    private DongnewsService service;

    // 목록
    @GetMapping("list")
    public String list(@ModelAttribute("cri") Criteria cri,
                       Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "admin/admin_dongnews_list";
    }

    // 상세보기
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
    public String write(DongnewsVO board,
                        @ModelAttribute("cri") Criteria cri,
                        RedirectAttributes rttr) {
        int result = service.insertBoard(board);
        if(result == 1) {
            return "redirect:/admin/dongnews/list";
        }
        else {
            rttr.addAttribute("pageNum", cri.getPageNum());
            rttr.addAttribute("amount", cri.getAmount());
            rttr.addAttribute("searchType", cri.getSearchType());
            rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
            rttr.addAttribute("errorMsg", "서버 오류입니다.\\n다시 시도해주세요.");
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
    public String update(@ModelAttribute("board") DongnewsVO board,
                         @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttr) {
        service.updateBoard(board);
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
}
