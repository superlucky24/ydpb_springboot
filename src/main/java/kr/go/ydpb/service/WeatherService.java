package kr.go.ydpb.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class WeatherService {
    @Value("${WEATHER_SERVICE_KEY}")
    private String SERVICE_KEY;
    private static final String API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
    private static final String DUST_API_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";

    @Setter(onMethod_ = @Autowired)
    private RestTemplate restTemplate;

    public String getWeather() {
        LocalDateTime now = LocalDateTime.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 30분 단위로 내림
        int baseMinute = (minute >= 30) ? 30 : 0;

        // 만약 00:00~00:29 사이면 전 시간으로 내림 (hour-1)
        if (minute < 30 && hour == 0) {
            // 날짜도 전날로 변경
            LocalDateTime yesterday = now.minusDays(1);
            year = yesterday.getYear();
            month = yesterday.getMonthValue();
            day = yesterday.getDayOfMonth();
            hour = 23;
            baseMinute = 30;
        } else if (minute < 30) {
            hour = hour - 1;
        }

        String baseDate = String.format("%04d%02d%02d", year, month, day);
        String baseTime = String.format("%02d%02d", hour, baseMinute);

        URI uri = UriComponentsBuilder
                .fromUriString(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", 59)
                .queryParam("ny", 126)
                .build(true)
                .toUri();

        return restTemplate.getForObject(uri, String.class);
    }

    public String getDust() {
        String stationName = URLEncoder.encode("영등포구", StandardCharsets.UTF_8);

        URI uri = UriComponentsBuilder
                .fromUriString(DUST_API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("returnType", "json")
                .queryParam("stationName", stationName)
                .queryParam("dataTerm", "DAILY")
                .queryParam("ver", "1.0")
                .build(true)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
