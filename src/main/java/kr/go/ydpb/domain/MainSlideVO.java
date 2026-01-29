package kr.go.ydpb.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class MainSlideVO {
    private Long slideId;
    private String title;
    private String imagePath; // 이미지 경로
    private String linkUrl;
    private int sortOrder; // 정렬 순위
    private String useYn; // 사용 여부
    private Date regDate;
}

