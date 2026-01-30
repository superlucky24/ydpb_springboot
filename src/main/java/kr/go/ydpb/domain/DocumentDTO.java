package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class DocumentDTO {
    private String userName;
    private String userBirthDate;
    private String userGender;
    // 스크립트에서 생일,성별을 통해 주민등록번호 가공해서 등록
    private String userRegistrationNo;
}