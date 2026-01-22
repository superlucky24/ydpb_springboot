package kr.go.ydpb.service;

import kr.go.ydpb.domain.NaverTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

public class NaverServiceImpl implements NaverService{
    @Override
    public String getAccessToken(String code, String state) {
        RestTemplate rt = new RestTemplate();

        String url = "https://nid.naver.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + "9KcfO7UpD8oqiXNlCdAq"
                + "&client_secret=" + "FAuvwARm60"
                + "&code=" + code
                + "&state=" + state;

        ResponseEntity<String> response = rt.getForEntity(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        NaverTokenResponse token =
                mapper.readValue(response.getBody(), NaverTokenResponse.class);

        return token.getAccess_token();
    }
}
