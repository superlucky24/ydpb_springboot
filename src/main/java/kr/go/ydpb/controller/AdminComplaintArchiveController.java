package kr.go.ydpb.controller;

import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.service.ComplaintArchiveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class AdminComplaintArchiveController {

    private final ComplaintArchiveService complaintArchiveService;

    @GetMapping("/admin/archive")
    public String getRpaWeeklyData (@RequestParam(value = "targetDate", required = false) String targetDate,
    Model model) {

        LocalDate referenceDay;
        if (targetDate != null && !targetDate.isEmpty()) {
            // 주소창에 ?targetDate=2026-02-10 처럼 넣으면 해당 날짜가 포함된 전주 데이터 추출
            referenceDay = LocalDate.parse(targetDate);
        } else {
            // 아무것도 없으면 원래대로 '오늘' 기준 전주 추출
            referenceDay = LocalDate.now();
        }

        // 1. referenceDay를 기준으로 이번 주 월요일을 먼저 찾음.
        // TemporalAdjusters를 사용하면 오늘이 월요일이면 오늘을, 아니면 지난 월요일을 가져옴.
        LocalDate thisMonday = referenceDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));


        // 2. 이번 주 월요일에서 7일을 빼서 '지난주 월요일' 정의
        LocalDate lastMonday = thisMonday.minusDays(7);
        // 지난주 금요일
        LocalDate lastFriday = lastMonday.plusDays(4);

        // 3. DB 조회를 위해 LocalDateTime으로 변환 (00:00:00 ~ 23:59:59)
        LocalDateTime start = lastMonday.atStartOfDay();
        LocalDateTime end = lastFriday.atTime(LocalTime.MAX);

        // 4. 해당 기간의 데이터 조회 (comDate 기준)
        // 파라미터로 start, end를 넘겨서 쿼리에서 BETWEEN으로 처리
        List<ComplaintArchiveVO> weeklyList = complaintArchiveService.getWeeklyArchive(start, end);

        model.addAttribute("archiveList", weeklyList);
        model.addAttribute("period", lastMonday + " ~ " + lastFriday); // RPA 확인용 제목

        return "admin/admin_complaint_archive";
    }

    // json 데이터 추출용 메서드
    @GetMapping("/admin/archive/json")
    @ResponseBody
    public List<ComplaintArchiveVO> getRpaWeeklyDataJson(
            @RequestParam(value = "targetDate", required = false) String targetDate) {

        LocalDate referenceDay;
        if (targetDate != null && !targetDate.isEmpty()) {
            referenceDay = LocalDate.parse(targetDate);
        } else {
            referenceDay = LocalDate.now();
        }

        LocalDate thisMonday = referenceDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate lastMonday = thisMonday.minusDays(7);
        LocalDate lastFriday = lastMonday.plusDays(4);

        LocalDateTime start = lastMonday.atStartOfDay();
        LocalDateTime end = lastFriday.atTime(LocalTime.MAX);

        // DB에서 데이터 가져오기
        return complaintArchiveService.getWeeklyArchive(start, end);
    }

}
