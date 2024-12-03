package whynotthis.domain.user.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import whynotthis.domain.jwt.JwtTokenProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private final int accessTokenValidTime = 10 * 60;    // 액세스 토큰의 유효기간은 10분
    private final int refreshTokenValidTime = 10 * 24 * 60 * 60;    // 리프레시 토큰의 유효기간은 10일

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 사용자 정보 추출
        String username = authentication.getName();

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(username);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // Redis에 저장
        storeTokensInRedis(username, accessToken, refreshToken);

        // 쿠키 설정
        response.addCookie(createCookie("username", username, refreshTokenValidTime));
        response.addCookie(createCookie("Authorization", accessToken, accessTokenValidTime));
        response.addCookie(createCookie("RefreshToken", refreshToken, refreshTokenValidTime));

        // 리다이렉트 경로 설정
        response.sendRedirect("http://localhost:3000/input");
    }

    // Redis에 토큰 저장 메서드
    private void storeTokensInRedis(String username, String accessToken, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("access:" + username, accessToken, 10, TimeUnit.MINUTES); // 액세스 토큰 저장
        valueOperations.set("refresh:" + username, refreshToken, 10, TimeUnit.DAYS);  // 리프레시 토큰 저장
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가능
        cookie.setSecure(false);   // HTTPS 환경에서는 true로 설정
        cookie.setPath("/");       // 모든 경로에서 접근 가능
        return cookie;
    }
}