package whynotthis.domain.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("Request URI: " + request.getRequestURI());

        // 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }
        String username = null;
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                } else if ("Authorization".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("RefreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        System.out.println(1);
        // 토큰 정보가 없으면 다음 필터로 이동
        if (username == null) {
            chain.doFilter(request, response);
            return;
        }
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // Redis에서 액세스 토큰 확인
        String redisAccessToken = valueOperations.get("access:" + username);
        if (accessToken != null && redisAccessToken != null && redisAccessToken.equals(accessToken)) {
            // 액세스 토큰이 유효한 경우 사용자 인증 설정
            authenticateUser(username, chain, request, response);
            return;
        }

        // Redis에서 리프레시 토큰 확인
        if (refreshToken != null) {
            String redisRefreshToken = valueOperations.get("refresh:" + username);
            if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
                // 리프레시 토큰이 유효한 경우 새로운 액세스 토큰 발급
                String newAccessToken = jwtTokenProvider.createAccessToken(username);
                valueOperations.set("access:" + username, newAccessToken, 10, TimeUnit.MINUTES); // Redis에 저장
                response.addCookie(createCookie("Authorization", newAccessToken, 10 * 60)); // 새 쿠키 설정

                // 사용자 인증 설정
                authenticateUser(username, chain, request, response);
                return;
            }
        }

        // 토큰이 없거나 유효하지 않으면 다음 필터로 이동
        chain.doFilter(request, response);
    }

    private void authenticateUser(String username, FilterChain chain, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        UserEntity userEntity = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        CustomUserDetails userDetails = new CustomUserDetails(userEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경에서 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}