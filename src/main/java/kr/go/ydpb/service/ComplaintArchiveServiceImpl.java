package kr.go.ydpb.service;

import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.domain.ComplaintVO;
import kr.go.ydpb.mapper.ComplaintArchiveMapper;
import kr.go.ydpb.mapper.ComplaintMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ComplaintArchiveServiceImpl implements ComplaintArchiveService {

    // 민원 아카이브 sql 처리 Mapper 주입
    private final ComplaintArchiveMapper complaintArchiveMapper;

    @Override
    public ComplaintVO getOneComplaintArchive(int comId) {
        // 하나의 민원 아카이브 조회
        ComplaintVO vo = complaintArchiveMapper.getOneComplaintArc(comId);
        // 하나의 민원 아카이브 리턴
        return vo;
    }

    @Override
    public List<ComplaintArchiveVO> getAllComplaintArchive() {
        List<ComplaintArchiveVO> list = complaintArchiveMapper.getAllComplaintArc();
        return list;
    }

    @Override
    public int insertComplaintArchive(ComplaintVO cvo) {
        return complaintArchiveMapper.insertComplaintArc(cvo);
    }

    @Override
    public void updateComplaintArchive(ComplaintVO cvo) {

        complaintArchiveMapper.updateComplaintArc(cvo);
    }

    @Override
    public void updateComplaintUserArchive(ComplaintVO cvo) {
        complaintArchiveMapper.updateComplaintUserArc(cvo);
    }

    @Override
    public int deleteComplaintArchive(int comId) {
        // 글번호에 해당하는 민원 아카이브 삭제 처리 메서드 실행
        complaintArchiveMapper.deleteComplaintArc(comId);
        // 삭제한 민원 아카이브 글번호 리턴
        return comId;
    }

    @Override
    public Map<String, Object> getWeeklyArchiveWithPeriod(String targetDate) {
        LocalDate referenceDay;
        if (targetDate != null && !targetDate.isEmpty()) {
            referenceDay = LocalDate.parse(targetDate);
        } else {
            referenceDay = LocalDate.now();
        }

        // 날짜 계산 (월요일 ~ 일요일)
        LocalDate thisMonday = referenceDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate lastMonday = thisMonday.minusWeeks(1);
        LocalDate lastSunday = thisMonday.minusDays(1);

        LocalDateTime start = lastMonday.atStartOfDay();
        LocalDateTime end = lastSunday.atTime(LocalTime.MAX);

        // 데이터 조회
        List<ComplaintArchiveVO> weeklyList = complaintArchiveMapper.getWeeklyArchive(start, end);

        // 결과물 포장
        Map<String, Object> result = new HashMap<>();
        result.put("list", weeklyList);
        result.put("period", lastMonday + " ~ " + lastSunday);

        return result;
    }
}
