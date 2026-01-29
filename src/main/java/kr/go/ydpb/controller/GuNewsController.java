package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.GuNewsVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.GuNewsService;
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
@RequestMapping("/gunews/*")
public class GuNewsController {
    @Setter(onMethod_ = @Autowired)
    private GuNewsService service;

    /* 목록 */
    @GetMapping("list")
    public String list(@ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("list", service.getList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        return "sub/gunews_list";
    }

    /* 상세 보기 */
    @GetMapping("view")
    public String view(@RequestParam("gnewsId") Long gnewsId, @ModelAttribute("cri") Criteria cri, Model model) {
        service.increaseCount(gnewsId);
        model.addAttribute("board", service.getBoard(gnewsId));
        model.addAttribute("prev", service.getPrev(gnewsId, cri));
        model.addAttribute("next", service.getNext(gnewsId, cri));
        return "sub/gunews_view";
    }

    // 최신글 불러오기
    @GetMapping(value = "recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<GuNewsVO>> recent() {
        List<GuNewsVO> list = service.getList(new Criteria(1, 5));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
