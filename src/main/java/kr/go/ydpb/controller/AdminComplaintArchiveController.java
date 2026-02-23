package kr.go.ydpb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.go.ydpb.domain.ComplaintArchiveVO;
import kr.go.ydpb.service.ComplaintArchiveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tools.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class AdminComplaintArchiveController {

    private final ComplaintArchiveService complaintArchiveService;
    private final ObjectMapper objectMapper;

    @GetMapping("/admin/archive")
    public String getRpaWeeklyData(@RequestParam(value = "targetDate", required = false) String targetDate,
                                   Model model) throws JsonProcessingException {

        LocalDate referenceDay;
        if (targetDate != null && !targetDate.isEmpty()) {
            // 주소창에 ?targetDate=2026-02-10 처럼 넣어서 그 이전 데이터도 출력가능
            referenceDay = LocalDate.parse(targetDate);
        } else {
            // 아무것도 없으면 원래대로 '오늘' 기준 전주 추출
            referenceDay = LocalDate.now();
        }


        // 1. referenceDay를 기준으로 이번 주 월요일을 먼저 찾음.
        // TemporalAdjusters를 사용하면 오늘이 월요일이면 오늘을, 아니면 지난 월요일을 가져옴.
        LocalDate thisMonday = referenceDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));


        // 2. 이번 주 월요일에서 7일을 빼서 '지난주 월요일' 정의
        LocalDate lastMonday = thisMonday.minusWeeks(1);
        // 지난주 금요일
        LocalDate lastSunday = thisMonday.minusDays(1);

        // 3. DB 조회를 위해 LocalDateTime으로 변환 (00:00:00 ~ 23:59:59)
        LocalDateTime start = lastMonday.atStartOfDay();
        LocalDateTime end = lastSunday.atTime(LocalTime.MAX);

        // 4. 해당 기간의 데이터 조회 (comDate 기준)
        // 파라미터로 start, end를 넘겨서 쿼리에서 BETWEEN으로 처리
        List<ComplaintArchiveVO> weeklyList = complaintArchiveService.getWeeklyArchive(start, end);

        // 필요한 6개 필드만 추출하여 Map 리스트 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<Map<String, Object>> filteredList = weeklyList.stream().map(arc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("민원인", arc.getMemName());
            map.put("아이디", arc.getMemId());
            map.put("신청일", sdf.format(arc.getComDate()));
            map.put("답변자", arc.getAnswerId());
            map.put("답변일", arc.getAnswerDate() != null ? sdf.format(arc.getAnswerDate()) : "-");
            map.put("민원제목", arc.getComTitle());
            map.put("민원내용", arc.getComContent());
            return map;
        }).collect(Collectors.toList());

        // 5. JSON 텍스트 출력용 문자열변환
        String jsonList = objectMapper.writeValueAsString(filteredList);

        model.addAttribute("archiveList", weeklyList);
        model.addAttribute("period", lastMonday + " ~ " + lastSunday); // RPA 확인용 제목
        model.addAttribute("jsonList", jsonList);

        return "admin/admin_complaint_archive";
    }

    // json 데이터페이지 메서드
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
        LocalDate lastMonday = thisMonday.minusWeeks(1);
        LocalDate lastSunday = thisMonday.minusDays(1);

        LocalDateTime start = lastMonday.atStartOfDay();
        LocalDateTime end = lastSunday.atTime(LocalTime.MAX);

        // DB에서 데이터 가져오기
        return complaintArchiveService.getWeeklyArchive(start, end);
    }

    /* 원하는 데이터만 나오게 하려면 */
    /*
    @GetMapping("/admin/archive/json")
    @ResponseBody
    public List<Map<String, Object>> getRpaWeeklyDataJson(
        @RequestParam(value = "targetDate", required = false) String targetDate) throws JsonProcessingException {

    LocalDate referenceDay;
    if (targetDate != null && !targetDate.isEmpty()) {
        referenceDay = LocalDate.parse(targetDate);
    } else {
        referenceDay = LocalDate.now();
    }

    LocalDate thisMonday = referenceDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    LocalDate lastMonday = thisMonday.minusWeeks(1);
    LocalDate lastSunday = thisMonday.minusDays(1);

    LocalDateTime start = lastMonday.atStartOfDay();
    LocalDateTime end = lastSunday.atTime(LocalTime.MAX);

    List<ComplaintArchiveVO> weeklyList = complaintArchiveService.getWeeklyArchive(start, end);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    return weeklyList.stream().map(arc -> {
        Map<String, Object> map = new HashMap<>();
        map.put("민원인", arc.getMemName());
        map.put("아이디", arc.getMemId());
        map.put("신청일", sdf.format(arc.getComDate()));
        map.put("답변일", arc.getAnswerDate() != null ? sdf.format(arc.getAnswerDate()) : "-");
        map.put("민원제목", arc.getComTitle());
        map.put("민원내용", arc.getComContent());
        return map;
    }).collect(Collectors.toList());
}
*/

}
