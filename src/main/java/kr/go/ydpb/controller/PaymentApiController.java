package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.DocumentDTO;
import kr.go.ydpb.domain.PaymentVO;
import kr.go.ydpb.mapper.PaymentMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

// JSON 데이터를 주고받는 API 전용 컨트롤러
@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentApiController {


    private final PaymentMapper paymentMapper;

    /* 결제 검증 및 DB 기록을 위한 API, 클라이언트(브라우저)에서 결제가 끝난 후 호출 */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data, HttpSession session) {
        String memId = (String) session.getAttribute("memId");

        // 고유 결제 ID와 시스템의 주문번호, 서류타입 가져옴
        String txId = (String) data.get("txId");
        String paymentId = (String) data.get("paymentId");
        String docType = (String) data.get("docType");
        int amount = Integer.parseInt(data.get("amount").toString());

        // 결제직전 로그인 이중 체크
        if (memId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");



        // 결제 내역 확인
        if (paymentMapper.checkPaymentExists(memId, docType) > 0) {
            return ResponseEntity.ok().body("이미 결제된 내역이 있습니다.");
        }

        // 1. DB 저장용 VO
        PaymentVO payment = new PaymentVO();
        payment.setMemId(memId);
        payment.setTxId(txId);
        payment.setPaymentId(paymentId);
        payment.setDocType(docType);
        payment.setAmount(amount);
        payment.setPayDate(LocalDateTime.now());

        int result = paymentMapper.insertPayment(payment);

        if (result > 0) {
            // 결제 정보 저장용
            DocumentDTO tempDoc = (DocumentDTO) session.getAttribute("tempDoc");
            if (tempDoc != null) {
                tempDoc.setDocType(payment.getDocType());
                tempDoc.setAmount(payment.getAmount());
                tempDoc.setPayDate(payment.getPayDate());
                // 세션에 다시 저장
                session.setAttribute("tempDoc", tempDoc);
            }
            return ResponseEntity.ok().body("결제내역 저장 완료");
        } else {
            return ResponseEntity.internalServerError().body("DB 저장 실패");
        }
    }
}