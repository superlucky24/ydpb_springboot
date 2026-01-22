package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.NaverUserResponse;
import kr.go.ydpb.service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NaverController {
//    private final NaverService naverService;


//    @GetMapping("/login/oauth2/code/naver")
//    public String naverAuth(@AuthenticationPrincipal OAuth2User user,
//                            HttpSession session
//    ) {
//        if(user!= null){
//            session.setAttribute("memId",
//                    ((Map<String, Object>) user.getAttributes().get("response"))
//                            .get("id") );
//        }
//
//        return "redirect:/";
//    }

//    @GetMapping("/")
//    public String index(Authentication authentication) {
//
//        OAuth2AuthenticationToken token =
//                (OAuth2AuthenticationToken) authentication;
//
//        System.out.println("ATTRIBUTES = " +
//                token.getPrincipal().getAttributes());
//
//        return "index";
//    }

    //네이버 로그아웃
    @GetMapping("/naver/logout")
    public String naverLogout(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            HttpSession session
    ) {

        String accessToken = (String) session.getAttribute("NAVER_ACCESS_TOKEN");

        if (accessToken != null) {
            // 네이버 토큰 삭제 요청
            String url = "https://nid.naver.com/oauth2.0/token"
                    + "?grant_type=delete"
                    + "&client_id=9KcfO7UpD8oqiXNlCdAq"
                    + "&client_secret=FAuvwARm60"
                    + "&access_token=" + accessToken
                    + "&service_provider=NAVER";

            // RestTemplate 또는 WebClient 호출
        }

        session.invalidate(); // 이중 안전
        return "redirect:/";
    }
}
