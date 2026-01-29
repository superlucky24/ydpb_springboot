package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class GalleryFileVO {

    private Long fileId;
    private Long galId;

    private String uuid;
    private String fileName;
    private String uploadPath; // 업로드 경로

    private String altText;      // 대체 텍스트
    private String insertYn;     // 'Y' / 'N'
}
