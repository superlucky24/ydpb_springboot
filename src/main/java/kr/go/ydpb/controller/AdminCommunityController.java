package kr.go.ydpb.controller;

import kr.go.ydpb.domain.CommunityVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.CommunityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/admin/community")
public class AdminCommunityController {
    @Autowired
    private CommunityService service;

    /* 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "admin/admin_community_center_list";
    }

    /* 글쓰기 페이지 */
    @GetMapping("/write")
    public String writeForm(Criteria cri, Model model) {
        model.addAttribute("board", new CommunityVO());
        model.addAttribute("cri", cri);
        return "admin/admin_community_center_write";
    }

    /* 글 등록 */
    @PostMapping("/write")
    public String register(CommunityVO board, @RequestParam("uploadFile") MultipartFile uploadFile, RedirectAttributes rttr) {

        log.info("register controller => {}", board);
        log.info("파일명 => {}", uploadFile.getOriginalFilename());
        log.info("파일 크기 => {}", uploadFile.getSize());

        service.register(board, uploadFile);

        rttr.addFlashAttribute("result", board.getCmntId());
        return "redirect:/admin/community/list"; // 저장 후 목록으로 이동
    }

    /* 상세 보기 */
    @GetMapping("/view")
    public String view(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri, Model model) {
        service.increaseCount(cmntId);
        model.addAttribute("board", service.get(cmntId));
        model.addAttribute("cri", cri);
        return "admin/admin_community_center_view";
    }

    /* 수정 페이지 */
    @GetMapping("/update")
    public String updateForm(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("board", service.get(cmntId));
        return "admin/admin_community_center_update";
    }

    /* 수정 처리 */
    @PostMapping("/update")
    public String update(@ModelAttribute("board") CommunityVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {

        // 수정 처리
        if(service.modify(board)) {
            rttr.addFlashAttribute("result", "success");
        }
        // 상세보기로 이동할 때 cmntId 쿼리 파라미터 전달
        rttr.addAttribute("cmntId", board.getCmntId());
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/community/view";
    }

    /* 삭제 페이지 */
    @PostMapping("/delete")
    public String deleteForm(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        log.info("remove => {}", cmntId);

        if(service.remove(cmntId)) {
            rttr.addFlashAttribute("result", "success"); // 성공 메시지만 전달
        }
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        // 삭제 후 목록 페이지로 redirect
        return "redirect:/admin/community/list";
    }
}
