//package whynotthis.domain.user.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import whynotthis.domain.user.repository.UserRepository;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2Service extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        //provider 정보 (Kakao, google, naver ... )
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
//        log.info("registrationId = {}", registrationId);
//        log.info("userNameAttributeName = {}", userNameAttributeName);
//
//        //provider 정보 기반 객체 생성
//
//    }
//}
