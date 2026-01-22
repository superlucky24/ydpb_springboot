package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class MainSlideVO {
    private Long slideId;
    private String title;
    private String imagePath;
    private String linkUrl;
    private int sortOrder;
    private String useYn;
    private Date regDate;
}

