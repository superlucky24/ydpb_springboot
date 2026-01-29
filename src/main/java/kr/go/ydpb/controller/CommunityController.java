package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.CommunityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/community/*")
public class CommunityController {
    @Autowired
    private CommunityService service;

    /* 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/community_center_list";
    }

    /* 상세 보기 */
    @GetMapping("/view")
    public String view(@RequestParam("cmntId") Long cmntId, @ModelAttribute("cri") Criteria cri, Model model) {
        // 조회수 증가
        service.increaseCount(cmntId);

        // 필요한 데이터 받음(board, prev, next)
        model.addAttribute("board", service.get(cmntId));
        model.addAttribute("prev", service.getPrev(cmntId, cri));
        model.addAttribute("next", service.getNext(cmntId, cri));
        return "sub/community_center_view";
    }
}
