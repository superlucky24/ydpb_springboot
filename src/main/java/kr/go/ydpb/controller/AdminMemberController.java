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

    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        int total = adminMemberService.getTotalCount(cri);
        model.addAttribute("list", adminMemberService.getMemberList(cri));
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        model.addAttribute("total", total);
        return "admin/admin_member_list";
    }

    // 2. view 컨트롤러에 Criteria 추가 (화이트라벨 에러 방지용)
    @GetMapping("/view")
    public String view(@RequestParam("memId") String memId, Criteria cri, Model model) {
        MemberVO member = adminMemberService.getMemberById(memId);
        model.addAttribute("member", member);
        model.addAttribute("cri", cri); // HTML에서 `${cri.pageNum}` 등을 쓸 수 있게 함
        return "admin/admin_member_view";
    }

    // 3. 비밀번호 수정 후 정보 유지
    @PostMapping("/updatePw")
    public String updatePw(@RequestParam("memId") String memId,
                           @RequestParam("memPassword") String memPassword,
                           Criteria cri, // 페이지 정보 받기
                           RedirectAttributes rttr) {

        int count = adminMemberService.updatePassword(memId, memPassword);

        if (count == 1) {
            rttr.addFlashAttribute("result", "pw_success");
        } else {
            rttr.addFlashAttribute("result", "pw_fail");
        }

        rttr.addAttribute("memId", memId);
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/member/view";
    }

    // 4. 삭제 후 원래 보던 페이지로 이동
    @GetMapping("/delete")
    public String delete(@RequestParam("memId") String memId, Criteria cri, RedirectAttributes rttr) {
        int count = adminMemberService.deleteMember(memId);

        if(count == 1) {
            rttr.addFlashAttribute("result", "success");
        }

        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());

        return "redirect:/admin/member/list";
    }
}