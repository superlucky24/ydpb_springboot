package kr.go.ydpb.service;

import kr.go.ydpb.domain.KakaoUserResponse;
import kr.go.ydpb.domain.MemberVO;
import kr.go.ydpb.domain.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

// OAuth2 로그인 성공 후 응답받은 데이터(사용자정보) 처리 - 귀환
@Service
@RequiredArgsConstructor
// OAuth2UserService <- OAuth2 로그인 성공 후 공급자(Kakao, Naver 등)로부터 받은 데이터를 처리하기 위한 표준 인터페이스를 구현
public class CustomOAuth2UserService  implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final JoinService joinService;

    @Override
    // loadUser : 리소스 서버(Naver 등)로부터 Access Token을 받은 후, 사용자 정보를 요청할 때 실행되는 메서드
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        System.out.println("CustomOAuth2UserService loadUser");

        // DefaultOAuth2UserService: 스프링 시큐리티의 기본 구현체
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 현재 로그인을 시도한 서비스가 어디인지 식별 값을 가져옴
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // oauth2User.getAttributes()를 통해 사용자 정보가 담긴 Map을 가져옴
        // registrationId 값에 따라(Kakao, Naver 등) 처리 방식 분기
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

        // Jackson 라이브러리의 클래스로, JSON 데이터를 Java 객체로 변환하거나 그 반대의 작업을 할 때 사용
        ObjectMapper objectMapper = new ObjectMapper();

        // 사용자 정보를 받아 관리할 변수 초기화
        MemberVO member = null;

//      네이버 로그인 처리
        if ("naver".equals(registrationId)) {

            //네이버 응답 데이터 중 실제 사용자 정보가 들어있는 response 키의 값만 추출
            attributes = (Map<String, Object>) oauth2User.getAttributes().get("response");
            // 네이버 응답형식 : {"resultcode": "00", "message": "success", "response": {"id": "...", "email": "..."}}
            // resultcode 같은 불필요한 정보 제외 .get("response")를 통해 실제 유저 프로필 데이터가 담긴 내부 Map만 따로 떼어내어 attributes 변수에 다시 할당
            // get() 메서드는 Object 타입을 반환하므로, 이를 다시 Map 구조로 다루기 위해 (Map<String, Object>)로 강제 형변환

            //Map 형태의 데이터를 미리 정의해둔 NaverUserResponse.Response라는 클래스(DTO) 객체로 변환
            NaverUserResponse.Response naverUser = objectMapper.convertValue(attributes, NaverUserResponse.Response.class);
            // objectMapper.convertValue => Jackson 라이브러리의 핵심 기능
            // attributes Map 안에 있는 key값들(id, email, name 등)을 NaverUserResponse.Response 클래스의 필드명과 매칭시켜서 값을 자동으로 채워넣어 줌
            // -> 매번 attributes.get("email")과 같이 문자열로 데이터를 꺼내올 필요가 없음
            // naverUser.getEmail() 방식 코드 작성 가능하여 오타로 인한 에러 줄여줌

            // 회원 등록 메서드 실행
            member = joinService.naverLoginOrJoin(naverUser);
        }

//      카카오 로그인 처리
        else if ("kakao".equals(registrationId)) {

            //카카오 응답 데이터 중 실제 사용자 정보가 들어있는 kakao_account 키의 값만 추출
            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");

            // kakao_account 내부에 닉네임, 프로필 이미지 등이 담긴 profile이라는 객체가 존재
            // 가져온 kakao_account 키에서 한 단계 더 들어가서 사용자 정보를 가져옴
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            // 기존에 사용자 정보 형식 맞게 만들어진 KakaoUserResponse 객체 활용
            // 로그인 요청으로부터 넘어온 사용자 정보를 할당
            KakaoUserResponse kakaoUser = new KakaoUserResponse(
                    //카카오의 고유 회원 번호인 id는 최상위 루트에 있으므로 oauth2User.getAttribute("id")로 바로 가져와 문자열로 변환
                    oauth2User.getAttribute("id").toString(),
                    //profile 맵에서 nickname을 꺼냄
                    (String) profile.get("nickname"),
                    //kakaoAccount 맵에서 email을 꺼냄
                    (String) kakaoAccount.get("email")
            );
            // 회원 등록 메서드 실행
            member = joinService.kakaoLoginOrJoin(kakaoUser);

            //표준화되지 않은 카카오의 데이터 nickname을 서비스 전체에서 공통으로 사용할 키값인 name으로 복사하여 저장
            attributes.put("name", (String)profile.get("nickname"));
        }
        else { // 등록 id 키워드와 일치하지 않으면 예외 처리
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth 제공자: " + registrationId);
        }

        // Spring Security 권한 부여
        String role = switch (member.getMemRole()) {
            case 0 -> "ROLE_USER";
            case 1 -> "ROLE_ADMIN";
            default -> throw new IllegalStateException("알 수 없는 권한: " + member.getMemRole());
        };

        System.out.println("OAuth ROLE => " + role);

        //모든 사용자 정보 파싱과 비즈니스 로직(회원가입/로그인)이 끝난 후, Spring Security가 관리할 최종적인 사용자 객체를 생성하여 반환
        // DefaultOAuth2User는 Spring Security에서 기본적으로 제공하는 OAuth2User 인터페이스의 구현체
        return new DefaultOAuth2User(
                //사용자가 가지는 권한(예: ROLE_USER, ROLE_ADMIN)을 Spring Security가 이해할 수 있는 형태의 객체로 만듦
                Collections.singleton(new SimpleGrantedAuthority(role)),
                attributes,// 네이버나 카카오로부터 추출하고 정규화(예: name 키 추가 등)했던 사용자 Map 정보
                "id" // 맵 안에서 사용자를 고유하게 식별할 수 있는 키(Key)를 지정
                // -> Spring Security는 이 키값을 통해 사용자의 이름(Principal Name)이 무엇인지 판단
        );
    }


}
