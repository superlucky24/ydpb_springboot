package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService  implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final JoinService joinService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        System.out.println("### CustomOAuth2UserService CALLED");

        OAuth2User oauth2User =
                new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId =
                userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> response = new HashMap<>(oauth2User.getAttributes());

        ObjectMapper objectMapper = new ObjectMapper();

        MemberVO member = null;
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        String nameAttributeKey = null;
//      네이버 로그인 처리
        if ("naver".equals(registrationId)) {

            attributes = (Map<String, Object>) oauth2User.getAttributes().get("response");

            NaverUserResponse.Response naverUser =
                    objectMapper.convertValue(attributes, NaverUserResponse.Response.class);

            member = joinService.naverLoginOrJoin(naverUser);

            nameAttributeKey = "id";
        }

//      카카오 로그인 처리
        else if ("kakao".equals(registrationId)) {

            Map<String, Object> kakaoAccount =
                    (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");

            Map<String, Object> profile =
                    (Map<String, Object>) kakaoAccount.get("profile");

            KakaoUserResponse kakaoUser = new KakaoUserResponse(
                    oauth2User.getAttribute("id").toString(),
                    (String) profile.get("nickname"),
                    (String) kakaoAccount.get("email")
            );

            member = joinService.kakaoLoginOrJoin(kakaoUser);

            attributes.put("name", (String)profile.get("nickname"));
            nameAttributeKey = "id";
        }
        else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth 제공자: " + registrationId);
        }

        String role = switch (member.getMemRole()) {
            case 0 -> "ROLE_USER";
            case 1 -> "ROLE_ADMIN";
            default -> throw new IllegalStateException("알 수 없는 권한: " + member.getMemRole());
        };
        System.out.println("### OAuth ROLE = " + role);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(role)),
                attributes,
                "id"
        );
    }

}
