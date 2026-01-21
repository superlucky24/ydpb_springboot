package kr.go.ydpb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
}
