package kr.go.ydpb.controller;

import jakarta.servlet.http.HttpSession;
import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.service.JoinService;
import kr.go.ydpb.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class KakaoController {
    private final KakaoService kakaoService;
    private final JoinService joinService;

    @GetMapping("/kakao/auth-code")
    public String loginForm(
            @RequestParam(required = false) String code, Model model
            , HttpSession session
            ){
        System.out.println("### kakao 인가 코드 요청");
        System.out.println("code: " + code);
//        System.out.println("error: " + error);
//        System.out.println("errorDescription: " + errorDescription);
//        System.out.println("state: " + state);

        //토큰 테스트
        String accessToken = kakaoService.getAccessToken(code);
        System.out.println("accessToken = " + accessToken);

        //유저 정보 요청 테스트
        KakaoUserResponse kakaoUser = kakaoService.getUserInfo(accessToken);

        System.out.println("카카오 ID = " + kakaoUser.getId());
        System.out.println("닉네임 = " +
                kakaoUser.getKakaoAccount().getProfile().getNickname());
        System.out.println("이메일 = " +
                kakaoUser.getKakaoAccount().getEmail());

        //로그인 메서드 실행
        MemberVO loginMember = joinService.kakaoLoginOrJoin(kakaoUser);
        //로그인 세션처리
        session.setAttribute("memId",loginMember.getMemId());
        session.setAttribute("admin",loginMember.getMemRole());

        return "redirect:/";
    }

}
