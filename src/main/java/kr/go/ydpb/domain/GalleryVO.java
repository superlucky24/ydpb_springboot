package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class GalleryVO {

    private long galId;
    private String galTitle;
    private Date galDate;
    private Date galUpdateDate;
    private String galDepartment;
    private String galContent;
    private int galCount;
    private String galTel;
    private String memId;

    // 첨부파일 목록 (수정/조회 화면용)
    //private List<GalleryFileVO> files;
}
