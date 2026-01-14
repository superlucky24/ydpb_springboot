package kr.go.ydpb.controller;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.CommunityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/admin/community/*")
public class CommunityController {
    private CommunityService service;
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        log.info("list => {}", cri);
        model.addAttribute("list", service.getList(cri));
        int total = service.getTotal(cri);
        log.info("total => {}", total);
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        return "admin/admin_community_center_list";
    }//end of list

    @PostMapping("/register")
    public String register(CommunityVO board, RedirectAttributes rttr) {
        log.info("register : {}", board);
        service.register(board);
        rttr.addFlashAttribute("result",board.getCmntId());
        return "redirect:/board/list";
    }
    @GetMapping("/register")
    public void register() {}
    @GetMapping({"/get","/modify"})
    public void get(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri,
                    Model model) {
        log.info("/get or /modify");
        model.addAttribute("board",service.get(cmntId));
    }
    @PostMapping("/modify")
    public String modify(CommunityVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        log.info("modify => {}", board);
        if(service.modify(board)) {
            rttr.addFlashAttribute("result","success");
        }
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type",cri.getType());
        rttr.addAttribute("keyword",cri.getKeyword());
        return "redirect:/board/list";
    }
    @PostMapping("/remove")
    public String remove(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttr) {
        log.info("remove => {}", cmntId);
        if(service.remove(cmntId)) {
            rttr.addFlashAttribute("result","success");
        }
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type",cri.getType());
        rttr.addAttribute("keyword",cri.getKeyword());
        return "redirect:/board/list";
    }
}
