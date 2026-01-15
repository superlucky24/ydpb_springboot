package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.AdminMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Autowired
    private AdminMemberService adminMemberService;

    // 1. 회원 목록 조회
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        int total = adminMemberService.getTotalCount(cri);
        model.addAttribute("list", adminMemberService.getMemberList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        model.addAttribute("total", total);
        return "admin/admin_member_list";
    }

    // 2. 회원 상세 정보
    @GetMapping("/view")
    public String view(@RequestParam("memId") String memId, Model model) {
        MemberVO member = adminMemberService.getMemberById(memId);
        model.addAttribute("member", member);
        return "admin/admin_member_view";
    }

    // 3. 비밀번호 수정 실행
    @PostMapping("/updatePw")
    public String updatePw(@RequestParam("memId") String memId,
                           @RequestParam("memPassword") String memPassword,
                           RedirectAttributes rttr) {

        int count = adminMemberService.updatePassword(memId, memPassword);

        if (count == 1) {
            rttr.addFlashAttribute("result", "pw_success");
        } else {
            rttr.addFlashAttribute("result", "pw_fail");
        }
        return "redirect:/admin/member/view?memId=" + memId;
    }

    // 4. 회원 삭제 실행
    @GetMapping("/delete")
    public String delete(@RequestParam("memId") String memId, RedirectAttributes rttr) {
        int count = adminMemberService.deleteMember(memId);

        if(count == 1) {
            rttr.addFlashAttribute("result", "success");
        }
        return "redirect:/admin/member/list";
    }
}