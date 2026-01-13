package kr.go.ydpb.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ComplaintVO {
    private int comId;
    private String comTitle;
    private Date comDate;
    private int comPublic;
    private String comStatus;
    private String comContent;
    private String comAnswer;
    private String memId;
}
