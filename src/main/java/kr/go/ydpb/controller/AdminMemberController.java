package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Autowired
    private MemberMapper memberMapper;

    // 1. 회원 목록 조회
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {

        // 검색 조건이 포함된 전체 개수
        int total = memberMapper.getTotalCount(cri);
        model.addAttribute("list", memberMapper.getMemberList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        model.addAttribute("total", total);

        return "admin/admin_member_list";
    }

    // 2. 회원 상세 정보
    @GetMapping("/view")
    public String view(@RequestParam("memId") String memId, Model model) {
        MemberVO member = memberMapper.getMemberById(memId);
        model.addAttribute("member", member);
        return "admin/admin_member_view";
    }

    // 3. 비밀번호 수정 실행
    @PostMapping("/updatePw")
    public String updatePw(@RequestParam("memId") String memId,
                           @RequestParam("memPassword") String memPassword) {
        memberMapper.updatePassword(memId, memPassword);
        // 수정 완료 후 다시 해당 회원의 상세보기로 보냄
        return "redirect:/admin/member/view?memId=" + memId;
    }

    // 4. 회원 삭제 실행
    @GetMapping("/delete")
    public String delete(@RequestParam("memId") String memId, RedirectAttributes rttr) {
        // 1. DB 삭제 처리
        memberMapper.deleteMember(memId);

        // 2. 삭제 완료 메시지
        rttr.addFlashAttribute("result", "success");

        // 3. 다시 목록으로 리다이렉트 (절대 경로 사용)
        return "redirect:/admin/member/list";
    }
}
