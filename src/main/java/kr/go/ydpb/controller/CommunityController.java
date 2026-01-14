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
@RequestMapping("/admin/community")
public class CommunityController {

    private CommunityService service;

    /* 목록 */
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker",
                new PageDTO(cri, service.getTotal(cri)));

        return "admin/admin_community_center_list";
    }

    /* 글쓰기 페이지 */
    @GetMapping("/register")
    public String writeForm(Model model) {
        model.addAttribute("board", new CommunityVO());
        return "admin/admin_community_center_write";
    }

    /* 글 등록 */
    @PostMapping("/register")
    public String register(CommunityVO board, RedirectAttributes rttr) {
        board.setMemId("admin"); // 임시 로그인 ID 세팅, 실제 로그인 정보로 바꿀 것
        log.info("register controller => {}", board);
        service.register(board);
        rttr.addFlashAttribute("result", board.getCmntId());
        return "redirect:/admin/community/list"; // 저장 후 목록으로 이동
    }

    /* 상세 보기 */
    @GetMapping("/view")
    public String view(@RequestParam("cmntId") Long cmntId,
                       @ModelAttribute("cri") Criteria cri,
                       Model model) {

        model.addAttribute("board", service.get(cmntId));
        return "admin/admin_community_center_view";
    }

    /* 수정 페이지 */
    @GetMapping("/update")
    public String updateForm(@RequestParam("cmntId") Long cmntId,
                             @ModelAttribute("cri") Criteria cri,
                             Model model) {

        model.addAttribute("board", service.get(cmntId));
        return "admin/admin_community_center_update";
    }

    /* 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute("board") CommunityVO board,
                         @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttr) {

        // 수정 시도
        if (service.modify(board)) {
            rttr.addFlashAttribute("result", "success");  // 성공 메시지 전달
        } else {
            rttr.addFlashAttribute("result", "fail");     // 실패 메시지 전달
        }

        // 수정 후 목록 페이지로 이동
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());

        return "redirect:/admin/community/list";
    }

    /* 삭제 페이지 */
    @PostMapping("/delete")
    public String deleteForm(@RequestParam("cmntId") Long cmntId,
                             @ModelAttribute("cri") Criteria cri,
                             RedirectAttributes rttr) {
        log.info("remove => {}", cmntId);

        if(service.remove(cmntId)) {
            rttr.addFlashAttribute("result", "success"); // 성공 메시지만 전달
        }

        // 삭제 후 목록 페이지로 redirect
        return "redirect:/admin/community/list";
    }
}
