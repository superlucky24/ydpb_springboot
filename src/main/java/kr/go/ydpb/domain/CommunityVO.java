package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class CommunityVO {

    private long cmntId;
    private String cmntTitle;
    private Date cmntDate;
    private Date cmntUpdatedate;
    private String cmntDepartment;
    private String cmntContent;
    private int cmntCount;
    private String memId;

    // ✅ 첨부파일 목록 (수정/조회 화면용)
    private List<CommunityFileVO> files;
}
