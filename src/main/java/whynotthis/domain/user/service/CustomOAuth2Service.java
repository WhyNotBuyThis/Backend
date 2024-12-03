package whynotthis.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import whynotthis.domain.user.UserRole;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.dto.KakaoResponse;
import whynotthis.domain.user.dto.OAuth2Response;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.domain.user.repository.UserRepository;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        OAuth2Response response;
        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            response = new KakaoResponse(oauth2User.getAttributes());
        } else {
            throw new IllegalArgumentException("사용할  수 없는 인증방법입니다.");
        }

        String provider = response.getProvider();
        String providerId = response.getProviderId();
        String username = provider + "_" + providerId; //중복이 발생하지 않도록 provider와 providerId를 조합
        log.info("OAuth2 login: provider={}, providerId={}, username={}", provider, providerId, username);
        // String email = response.getEmail();

        Optional<UserEntity> byUsername = userRepository.findByUserEmail(username);
        UserEntity userEntity = null;
        if (byUsername.isEmpty()) {
            userEntity = UserEntity.builder()
                    .userEmail(username)
                    // .email(email)
                    .role(UserRole.ROLE_USER)
                    .build();

            userRepository.save(userEntity);
        } else {
            userEntity = byUsername.get();
        }

        return new CustomUserDetails(userEntity);
    }
}