package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class CommunityVO {
    private long cmntId;
    private String cmnttitle;
    private Date cmntDate;
    private Date cmntUpdatedate;
    private String cmntDepartment;
    private String cmntContent;
    private int cmntCount;
    private String memId;



}

