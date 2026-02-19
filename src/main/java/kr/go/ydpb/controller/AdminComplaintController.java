package kr.go.ydpb.controller;

import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.domain.Criteria;
import kr.go.ydpb.domain.PageDTO;
import kr.go.ydpb.service.ComplaintArchiveService;
import kr.go.ydpb.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

// 관리자 민원 처리 컨트롤러 - 귀환
@Controller
@RequestMapping("admin/complaint")
@AllArgsConstructor
public class AdminComplaintController {
    //주입
    @Setter(onMethod_ = @Autowired)
    private ComplaintService complaintService;

    //아카이브
    @Setter(onMethod_ = @Autowired)
    private ComplaintArchiveService complaintArchiveService;

    //목록 요청 처리 컨트롤러
    @GetMapping("list")
    public String complaintList(Model model, Criteria cri){
        // 페이징 기능 적용한 민원 리스트 생성
        List<ComplaintVO> complaintList = complaintService.getComplaintWithPaging(cri);
        if(complaintList==null){
            // 리스트값이 없으면 새 리스트 생성
            complaintList= new ArrayList<>();
        }
        // 처리된 민원 리스트 모델에 바인딩
        model.addAttribute("complaintList", complaintList);
        // 민원 총 갯수 확보
        int total = complaintService.getAllCount(cri);
        if(cri.getSearchType()!=null){
            // 따로 입력한 타입 없으면 모든 민원 갯수 확보
            total = complaintService.getAllSearchCount(cri);
        }
        // 페이징용 Criteria 정보와 민원 총 갯수를 기반으로하는 pageDTO 모델에 바인딩
        model.addAttribute("pageMaker",new PageDTO(cri,total));

        // 목록 화면을 실행
        return "admin/admin_complaint_list";
    }

    // 민원 상세보기 데이터만 가져옴
    @GetMapping("view")
    // 클릭한 민원 글 번호, 이전 페이지에서 보내준 cri, Model 객체,리다이렉트 시 바인딩용 RedirectAttributes
    public String complaintView(@RequestParam("comId") int comId,
                                @ModelAttribute("cri") Criteria cri, Model model,
                                RedirectAttributes rttr){
        // 받아온 글번호 파라미터로 하나의 글 가져와 모델에 바인딩
        model.addAttribute("complaint",complaintService.getOneComplaint(comId));
        // 페이징 정보 모델에 바인딩
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        // 검색 정보 모델에 바인딩
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        rttr.addAttribute("searchType", cri.getSearchType());
        // 상세보기 화면 실행
        return "admin/admin_complaint_view";
    }
    // 수정 화면 요청 처리
    @GetMapping("update")
    // 글번호, 페이징용 cri, Model
    public String getComplaintUpdate(@RequestParam("comId") int comId,
                                @ModelAttribute("cri") Criteria cri, Model model){
        // 글번호 파라미터로 하나의 민원 가져와 모델에 바인딩
        model.addAttribute("complaint",complaintService.getOneComplaint(comId));
        // 수정 화면 실행
        return "admin/admin_complaint_update";
    }

    // 수정 요청 처리
    @PostMapping("update")
    // 수정 데이터 담긴 ComplaintVO, , 페이징용 cri , 리다이렉트 시 바인딩용 RedirectAttributes
    public String complaintUpdate(ComplaintVO vo , @ModelAttribute Criteria cri , RedirectAttributes rttr){
        // 넘어온 민원 정보로 수정 sql 실행
        complaintService.updateComplaint(vo);

        // 아카이브 추가
        complaintArchiveService.updateComplaintArchive(vo);

        // 페이징 정보 RedirectAttributes에 바인딩
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        // 수정한 글 상세보기로 가기 위해 글번호 바인딩
        rttr.addAttribute("comId", vo.getComId());
        // 검색 정보 바인딩
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        rttr.addAttribute("searchType", cri.getSearchType());
        // 상세보기 화면 실행
        return "redirect:/admin/complaint/view";
    }
    // 삭제 요청 처리
    @PostMapping("delete")
    // 삭제용 글번호, 페이징용 cri, 리다이렉트 시 바인딩용 RedirectAttributes
    public String complaintDelete(@RequestParam("comId") int comId,
                                  @ModelAttribute ("cri") Criteria cri,
                                  RedirectAttributes rttr) {
        // 글번호를 이용, 해당 글 삭제 실행
        complaintService.deleteComplaint(comId);

        //아카이브 추가
        complaintArchiveService.deleteComplaintArchive(comId);

        // 페이징 정보 RedirectAttributes에 바인딩
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        // 검색 정보 바인딩
        rttr.addAttribute("searchKeyword", cri.getSearchKeyword());
        rttr.addAttribute("searchType", cri.getSearchType());

        // 삭제 후 목록 화면 실행
        return "redirect:/admin/complaint/list";
    }

    // 최신글 불러오기
    @GetMapping(value = "recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComplaintVO>> recent() {
        List<ComplaintVO> list = complaintService.getComplaintWithPaging(new Criteria(1, 5));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}

