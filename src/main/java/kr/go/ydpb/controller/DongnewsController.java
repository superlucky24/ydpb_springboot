package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.DongnewsService;
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
@AllArgsConstructor
@RequestMapping("/dongnews/*")
public class DongnewsController {
    @Setter(onMethod_ = @Autowired)
    private DongnewsService service;

    /* 목록 */
    @GetMapping("list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/dongnews_list";
    }

    /* 상세 보기 */
    @GetMapping("view")
    public String view(@RequestParam("dnewsId") Long dnewsId, @ModelAttribute("cri") Criteria cri, Model model) {
        System.out.println(service.getList(cri));
        service.increaseCount(dnewsId);
        model.addAttribute("board", service.getBoard(dnewsId));
        model.addAttribute("prev", service.getPrev(dnewsId, cri));
        model.addAttribute("next", service.getNext(dnewsId, cri));
        return "sub/dongnews_view";
    }
}
