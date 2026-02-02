package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.DocumentDTO;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sub")
public class SubPageController {

    @GetMapping("/status")
    public String status(){
        return "sub/status";
    }

    @GetMapping("/location")
    public String location() {

        return "sub/location";
    }

    @GetMapping ("/complaint_sector")
    public String complaintSector(){

        return "sub/complaint_sector";
    }

    @GetMapping("/hunjang")
    public String hunjang(){
        return "sub/hunjang";
    }
    @GetMapping("/business_guide")
    public String businessGuide(){
        return "sub/business_guide";
    }

}
