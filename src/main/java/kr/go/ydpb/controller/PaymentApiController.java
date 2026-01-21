package kr.go.ydpb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        String impUid = data.get("imp_uid");
        String merchantUid = data.get("merchant_uid");

        // 여기서 원래는 PortOne API를 호출해 실제 결제 내역을 조회해야 합니다.
        // 연습용이므로 로그만 찍고 성공을 보냅니다.
        System.out.println("결제 성공 DB 기록 시작 -> 주문번호: " + merchantUid);

        // TODO: DB에 '결제 완료' 상태 저장 로직 (Service 호출)

        return ResponseEntity.ok().body("결제 검증 완료");
    }
}