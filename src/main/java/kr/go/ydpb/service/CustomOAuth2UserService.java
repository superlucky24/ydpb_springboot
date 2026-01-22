package kr.go.ydpb.service;

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

        Map<String, Object> response =
                (Map<String, Object>) oauth2User.getAttributes().get("response");

        ObjectMapper objectMapper = new ObjectMapper();

        NaverUserResponse.Response naverUser =
                objectMapper.convertValue(response, NaverUserResponse.Response.class);

        NaverUserResponse nur = new NaverUserResponse();

        // DB 연동
        MemberVO member = joinService.naverLoginOrJoin(naverUser);
        String role = switch (member.getMemRole()) {
            case 0 -> "ROLE_USER";
            case 1 -> "ROLE_ADMIN";
            default -> throw new IllegalStateException("알 수 없는 권한: " + member.getMemRole());
        };
        System.out.println("### OAuth ROLE = " + role);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(role)),
                response,
                "id"
        );
    }

}
