package kr.go.ydpb.controller;

import kr.go.ydpb.service.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @Setter(onMethod_ = @Autowired)
    private AdminMemberService adminMemberService;

    @Setter(onMethod_ = @Autowired)
    private ComplaintService complaintService;

    @Setter(onMethod_ = @Autowired)
    private DongNewsService dongNewsService;

    @Setter(onMethod_ = @Autowired)
    private GuNewsService guNewsService;

    @Setter(onMethod_ = @Autowired)
    private GalleryService galleryService;

    @Setter(onMethod_ = @Autowired)
    private CommunityService communityService;



    @GetMapping(value = {"", "/", "/home"})
    public String adminHome() {
        return "/admin/admin_index";
    }

    // 일정 기간 개수 조회
    @GetMapping(value = "/statistics/period/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Map<String, String>> period(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, String> map = new HashMap<>();
        map.put("member", String.valueOf(adminMemberService.getCountPeriod(startDate, endDate)));
        map.put("complaint", String.valueOf(complaintService.getCountPeriod(startDate, endDate)));
        map.put("dongNews", String.valueOf(dongNewsService.getCountPeriod(startDate, endDate)));
        map.put("guNews", String.valueOf(guNewsService.getCountPeriod(startDate, endDate)));
        map.put("gallery", String.valueOf(galleryService.getCountPeriod(startDate, endDate)));
        map.put("community", String.valueOf(communityService.getCountPeriod(startDate, endDate)));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
