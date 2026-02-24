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

        Map<String, Object> result = complaintArchiveService.getWeeklyArchiveWithPeriod(targetDate);

        List<ComplaintArchiveVO> weeklyList = (List<ComplaintArchiveVO>) result.get("list");
        String period = (String) result.get("period");

        // 필요한 7개 필드만 추출하여 Map 리스트 생성
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

        model.addAttribute("archiveList", weeklyList);
        model.addAttribute("period", period);
        model.addAttribute("jsonList", objectMapper.writeValueAsString(filteredList));

        return "admin/admin_complaint_archive";
    }

    // json 데이터페이지 메서드
    @GetMapping("/admin/archive/json")
    @ResponseBody
    public List<ComplaintArchiveVO> getRpaWeeklyDataJson(
            @RequestParam(value = "targetDate", required = false) String targetDate) {

        // 동일한 서비스 메서드 재사용
        Map<String, Object> result = complaintArchiveService.getWeeklyArchiveWithPeriod(targetDate);
        return (List<ComplaintArchiveVO>) result.get("list");
    }

}
