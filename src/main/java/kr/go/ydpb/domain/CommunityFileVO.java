package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class CommunityFileVO {

    private Long fileId;
    private Long cmntId;

    private String uuid;
    private String fileName;
    private String uploadPath;  // 업로드 경로

    private String altText;      // 대체 텍스트
    private String insertYn;     // 'Y' / 'N' 형태
}
