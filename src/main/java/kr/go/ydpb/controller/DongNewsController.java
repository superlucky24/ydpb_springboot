package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.DongNewsVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.DongNewsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/dongnews/*")
public class DongNewsController {
    @Setter(onMethod_ = @Autowired)
    private DongNewsService service;

    // 목록
    @GetMapping("list")
    public String list(@ModelAttribute("cri") Criteria cri,
                       Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/dongnews_list";
    }

    // 글보기
    @GetMapping("view")
    public String view(@RequestParam("dnewsId") Long dnewsId,
                       @ModelAttribute("cri") Criteria cri,
                       Model model) {
        service.increaseCount(dnewsId);
        model.addAttribute("board", service.getBoard(dnewsId));
        model.addAttribute("prev", service.getPrev(dnewsId, cri));
        model.addAttribute("next", service.getNext(dnewsId, cri));
        return "sub/dongnews_view";
    }

    // 최신글 불러오기
    @GetMapping(value = "recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DongNewsVO>> recent() {
        List<DongNewsVO> list = service.getList(new Criteria(1, 5));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
