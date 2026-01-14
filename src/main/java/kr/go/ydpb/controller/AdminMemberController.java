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

import java.util.List;

@Controller
@RequestMapping("/admin/member") // 브라우저 주소창에 들어갈 기본 주소
public class AdminMemberController {

    @Autowired
    private MemberMapper memberMapper;

    // 1. 회원 목록 조회
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        // 1. DB에서 해당 페이지 분량만큼만 가져오기
        List<MemberVO> list = memberMapper.getMemberList(cri);
        model.addAttribute("list", list);

        // 2. 전체 개수 구하기
        int total = memberMapper.getTotalCount(cri);

        // 3. PageDTO(계산기)에 넣어 결과표(pageMaker) 만들기
        model.addAttribute("pageMaker", new PageDTO(cri, total));

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
        // 수정 완료 후 다시 해당 회원의 상세보기로 보냄 (리다이렉트)
        return "redirect:/member/view?memId=" + memId;
    }

    // 4. 회원 삭제 실행
    @PostMapping("/delete")
    public String delete(@RequestParam("memId") String memId) {
        memberMapper.deleteMember(memId);
        // 삭제 완료 후 목록으로 보냄 (리다이렉트)
        return "redirect:/member/list";
    }
}
