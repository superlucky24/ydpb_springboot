package kr.go.ydpb.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

    public boolean isRecent() {
        if (this.dnewsDate == null) {
            return false;
        }
        LocalDateTime writeDateTime = this.dnewsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return writeDateTime.isAfter(LocalDateTime.now().minusHours(24));
    }
}
