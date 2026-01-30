package kr.go.ydpb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// JSON 데이터를 주고받는 API 전용 컨트롤러
@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    /* 결제 검증 및 DB 기록을 위한 API, 클라이언트(브라우저)에서 결제가 끝난 후 호출 */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        // 포트원에서 생성한 고유 결제 ID와 우리 시스템의 주문번호 가져옴
        String impUid = data.get("imp_uid");
        String merchantUid = data.get("merchant_uid");

        // 여기서 원래는 PortOne API를 호출해 실제 결제 내역을 조회해야함.
        // 연습용이므로 로그만 찍고 성공을 보냄.
        System.out.println("결제 성공 DB 기록 시작 -> 주문번호: " + merchantUid);

        // 이곳에 결제 내역 관리하는 테이블에 데이터 저장하는 코드 들어가야함

        return ResponseEntity.ok().body("결제 검증 완료");
    }
}