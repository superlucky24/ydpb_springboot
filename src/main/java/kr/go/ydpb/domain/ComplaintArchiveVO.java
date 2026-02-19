package kr.go.ydpb.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ComplaintArchiveVO {
    private int comId;
    private String comTitle;
    private Date comDate;
    private int comPublic;
    private String comStatus;
    private String comContent;
    private String comAnswer;
    private String memId;

    // 아카이브용 컬럼 추가
    private String answerId;
    private Date answerDate;
    private int answerPeriod;
    private int deleteYn;
}
