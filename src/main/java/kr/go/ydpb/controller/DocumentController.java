package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.DocumentDTO;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.PaymentVO;
import kr.go.ydpb.mapper.PaymentMapper;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/* 결제 및 문서 출력 페이지 컨트롤러 */
@Controller
@RequestMapping("/document")
public class DocumentController {

    // 가족관계증명서 신청폼 데이터를 받기 위함
    private final MemberService memberService;
    private final PaymentMapper paymentMapper;

    @Value("${portone.store.id}")
    private String storeId;

    @Value("${portone.channel.key}")
    private String channelKey;

    // Value와 AllArgsConstructor 충돌해서 수동 입력
    public DocumentController(MemberService memberService, PaymentMapper paymentMapper) {
        this.memberService = memberService;
        this.paymentMapper = paymentMapper;
    }

    // /있을때 없을때 구분되어 둘다 처리, 기본경로 접속시 신청페이지로 연결
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/document/request";
    }

    /* 1. 문서 신청 폼 화면 */
    @GetMapping("/request")
    public String documentRequest(HttpSession session, Model model) {
        String memId = (String) session.getAttribute("memId");

        if (memId == null) {

            // 스크립트에서 처리
//            return "redirect:/login";
        }


        // 공용화용 변수 프론트와 동일하게 수정
        String currentDoc = "가족관계증명서";

        // 이미 결제한 내역이 있는지 확인 (중복 결제 방지) + 에러 방지용 기본값 세팅
        int alreadyPaid = (memId != null) ? paymentMapper.checkPaymentExists(memId, currentDoc) : 0;
        MemberVO member = (memId != null) ? memberService.getMemberById(memId) : null;
        model.addAttribute("member", member);
        // 결제 여부 전달
        model.addAttribute("alreadyPaid", alreadyPaid > 0);
        model.addAttribute("docType", currentDoc);
        model.addAttribute("portoneStoreId", storeId);
        model.addAttribute("portoneChannelKey", channelKey);

        return "sub/document_request";
    }
    /* 2. 신청 데이터 세션 저장 */
    @PostMapping("/complete")
    public String processComplete(DocumentDTO docDto, HttpSession session) {
        String memId = (String) session.getAttribute("memId");

        // 프론트에서 hidden으로 보냄
        String currentType = docDto.getDocType();

        // 1. 이미 결제한 사람이라서 payDate가 비어있다면 DB에서 조회
        if (docDto.getPayDate() == null && memId != null && currentType != null) {

            // DB에서 해당 유저의 '현재 서류 종류'에 해당하는 마지막 결제 내역 조회
            PaymentVO lastPay = paymentMapper.getLatestPayment(memId, currentType);

            if (lastPay != null) {
                docDto.setDocType(lastPay.getDocType());
                docDto.setAmount(lastPay.getAmount());
                docDto.setPayDate(lastPay.getPayDate());
            }
        }

        // 2. 세션에 임시 저장
        session.setAttribute("tempDoc", docDto);
        // 새로고침시 중복 제출 방지
        return "redirect:/document/complete";
    }

    /* 3. 신청 완료 및 데이터 확인 화면 */
    @GetMapping("/complete")
    public String showComplete(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");
        String memId = (String) session.getAttribute("memId");

        // 비정상적인 접근 방지
        if (docDto == null && memId != null) {
            // 다시 신청 페이지로
            return "redirect:/document/request";
        }

        model.addAttribute("doc", docDto);
        return "sub/document_complete";
    }

    /* 4. 문서 출력용 화면 */
    @GetMapping("/print")
    public String showPrint(HttpSession session, Model model) {
        DocumentDTO docDto = (DocumentDTO) session.getAttribute("tempDoc");

        // 보안 체크
        if (docDto == null) {
            return "redirect:/document/request";
        }

        model.addAttribute("doc", docDto);
        return "sub/document_print";
    }
}