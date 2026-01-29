package kr.go.ydpb.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GuNewsVO {
    private long gnewsId;
    private String gnewsTitle;
    private String gnewsDepartment;
    private String gnewsTel;
    private String gnewsContent;
    private Date gnewsDate;
    private Date gnewsUpdatedate;
    private int gnewsCount;
    private String gnewsOpentype;
    private String memId;
    private List<GuNewsFileVO> files = new ArrayList<>();

    public boolean isRecent() {
        if (this.gnewsDate == null) {
            return false;
        }
        LocalDateTime writeDateTime = this.gnewsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return writeDateTime.isAfter(LocalDateTime.now().minusHours(24));
    }
}
