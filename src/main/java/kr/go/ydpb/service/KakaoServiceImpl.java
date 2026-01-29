package kr.go.ydpb.service;


import kr.go.ydpb.domain.KakaoTokenResponse;
import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Service
public class KakaoServiceImpl implements KakaoService{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KakaoServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getAccessToken(String code) {

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "cf56d09c69aad48fda8e1e99ee73a18e");
        params.add("redirect_uri", "http://localhost:8888/kakao/auth-code");
        params.add("client_secret", "REdtl1CvooprsyAL4lzi2R8fScydy2nE");
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                tokenUrl,
                request,
                String.class
        );

        //  JSON -> DTO 변환
        try {
            KakaoTokenResponse tokenResponse =
                    objectMapper.readValue(response.getBody(), KakaoTokenResponse.class);

            return tokenResponse.getAccessToken();

        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 파싱 실패", e);
        }
    }

    @Override
    public KakaoUserResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), KakaoUserResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패", e);
        }
    }

}
