package kr.go.ydpb.controller;

import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.AdminMemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/* 관리자 페이지 회원목록 컨트롤러 */
@Controller
@AllArgsConstructor
@RequestMapping("/admin/member")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    // 1. 관리자용 회원 목록 조회 (페이징 및 검색 처리 포함)
    @GetMapping("/list")
    public String list(Criteria cri, Model model) {
        // 검색 조건에 맞게 전체 회원수 파악하여 페이징 계산
        int total = adminMemberService.getTotalCount(cri);
        model.addAttribute("list", adminMemberService.getMemberList(cri));
        // 페이지 번호
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        // 상단 전체 회원 수 표시용
        model.addAttribute("total", total);
        return "admin/admin_member_list";
    }

    // 2. view 컨트롤러에 Criteria 추가 (화이트라벨 에러 방지용)
    @GetMapping("/view")
    public String view(@RequestParam("memId") String memId, Criteria cri, Model model) {
        MemberVO member = adminMemberService.getMemberById(memId);
        model.addAttribute("member", member);
        // 목록으로 돌아갈때 내가 보던 페이지로 돌아가기 위함
        model.addAttribute("cri", cri);
        return "admin/admin_member_view";
    }

    // 3. 비밀번호 수정 후 정보 유지
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
            value = "/updatepw",
            consumes = "application/json",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> updatePw(@RequestBody MemberVO member) {
        int result = adminMemberService.updatePassword(member.getMemId(), member.getMemPassword());
        return (result == 1)
                ? new ResponseEntity<>("비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK)
                : new ResponseEntity<>("비밀번호 변경에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* 뒤로가기 할때 알림창 뜨는 문제를 해결하기위해 기존 방식에서 REST API 방식으로 수정*/
    /*@PostMapping("/updatePw")
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
    }*/

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

    // 가장 최근에 가입한 회원 목록
    @GetMapping(value = "recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberVO>> recent() {
        List<MemberVO> list = adminMemberService.getMemberList(new Criteria(1, 5));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}