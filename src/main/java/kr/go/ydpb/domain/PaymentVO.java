package kr.go.ydpb.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private Long payNo;            // PK
    private String memId;          // 결제한 회원 아이디
    private String txId;         // 포트원 결제 번호
    private String paymentId;    // 우리쪽 주문 번호 (DOC_...)
    private String docType;        // 서류 종류
    private int amount;            // 결제 금액
    private LocalDateTime payDate; // 결제 일시
}