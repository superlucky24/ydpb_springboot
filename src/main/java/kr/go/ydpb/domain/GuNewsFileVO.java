package kr.go.ydpb.domain;

import lombok.Data;

@Data
public class GuNewsFileVO {
    private Long fileId;
    private String uuid;
    private String fileName;
    private String uploadPath;
    private String altText;
    private String insertYn;
    private Long gnewsId;

    public String getFileType() {
        String type = null;
        if(this.fileName != null && !this.fileName.isBlank()) {
            String[] fileArr = this.fileName.split("\\.");
            type = fileArr[fileArr.length - 1];
        }
        return type;
    }
}