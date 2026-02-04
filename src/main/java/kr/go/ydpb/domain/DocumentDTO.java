package kr.go.ydpb.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private String userName;
    private String userBirthDate;
    private String userGender;
    // 스크립트에서 생일,성별을 통해 주민등록번호 가공해서 등록
    private String userRegistrationNo;

    // 결제 연동 필드
    private String docType;        // 서류 종류 (가족관계증명서 등)
    private int amount;            // 결제 금액
    private LocalDateTime payDate; // 결제 일시
}