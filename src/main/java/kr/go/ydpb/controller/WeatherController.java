package kr.go.ydpb.controller;

import kr.go.ydpb.service.WeatherService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/weather/")
public class WeatherController {
    @Setter(onMethod_ = @Autowired)
    private WeatherService weatherService;
    
    // 날씨 정보 json 형식 반환
    @GetMapping(value = "status", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> weather() {
        return new ResponseEntity<>(weatherService.getWeather(), HttpStatus.OK);
    }
    
    // 미세먼지 정보 json 형식으로 반환
    @GetMapping(value = "dust", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> dust() {
        return new ResponseEntity<>(weatherService.getDust(), HttpStatus.OK);
    }
}
