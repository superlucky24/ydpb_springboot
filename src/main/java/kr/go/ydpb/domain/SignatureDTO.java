package kr.go.ydpb.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class SignatureDTO {

    // 1. 신고인 정보 (JS의 formData와 매칭)
    @NotBlank(message = "신고인 성명을 입력해주세요.")
    private String reporterName;

    private String reporterJumin;
    private String reporterRel;
    private String reporterTel;
    private String reporterPhone;
    private String reporterAddr;

    // 2. 신고 사항
    private String reportContent;
    private String prevMaster;
    private String currMaster;

    // 3. 대상자 인적사항
    private List<TargetRowDTO> targets;

    // 4. 체크박스 상태 (활성화된 클래스명 수집용)
    private String topType;
    private String midType;
    private String btmType;

    // 5. 날짜 정보
    private String submitYear;
    private String submitMonth;
    private String submitDay;

    // 6. 위임하는 사람
    private String delegateName;

    // 7. 서명 데이터 (Base64 문자열)
    private String sigReporter;   // 신고인 서명
    private String sigDelegate;   // 대리인 서명
    private String sigPrev;       // 전 세대주 서명
    private String sigCurr;       // 현 세대주 서명

// 대상자 행 데이터를 담기 위한 내부 클래스
    @Data
    public static class TargetRowDTO {
        private String rel;    // 관계
        private String name;   // 성명
        private String jumin;  // 주민번호
        private String pre;    // 정정전
        private String post;   // 정정후
    }
}