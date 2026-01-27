package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
public class DongNewsVO {
    private long dnewsId;
    private String dnewsTitle;
    private String dnewsDepartment;
    private String dnewsTel;
    private String dnewsContent;
    private Date dnewsDate;
    private Date dnewsUpdatedate;
    private int dnewsCount;
    private String memId;
    private List<DongNewsFileVO> files = new ArrayList<>();
}
