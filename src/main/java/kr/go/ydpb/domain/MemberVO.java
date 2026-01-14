package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class MemberVO {

    private String memId;
    private String memName;
    private Date memBirth;
    private String memGender;
    private String memPassword;
    private String memAddress;
    private String memAddressDetail;
    private String memTel;
    private String memPhone;
    private String memEmail;
    private String memNews;
    private int memRole;
    // 가입일 추가
    private java.util.Date memRegDate;
}
