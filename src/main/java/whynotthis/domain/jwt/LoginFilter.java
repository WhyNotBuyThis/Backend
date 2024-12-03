package whynotthis.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        super.setFilterProcessesUrl("/user/login");
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> authRequest = objectMapper.readValue(request.getInputStream(), Map.class);
            String userEmail = authRequest.get("userEmail");
            String userPw = authRequest.get("userPw");

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userEmail);
            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found with email: " + userEmail);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, userPw);
            return authenticationManager.authenticate(authToken);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(customUserDetails.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(customUserDetails.getUsername());

        ValueOperations<String, String> valueOperations =  redisTemplate.opsForValue();
        valueOperations.set("access:" + customUserDetails.getUsername(), accessToken, 10, TimeUnit.MINUTES);
        valueOperations.set("refresh:" + customUserDetails.getUsername(), refreshToken, 10, TimeUnit.DAYS);

        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 쿠키 생성 및 추가
        Cookie Authorization = createCookie("Authorization", accessToken, 10 * 60); // 10분
        Cookie refreshTokenCookie = createCookie("RefreshToken", refreshToken, 10 * 24 * 60 * 60); // 10일
        Cookie usernameCookie = createCookie("username", customUserDetails.getUsername(), 10 * 24 * 60 * 60); // 10일


        response.addCookie(Authorization);
        response.addCookie(refreshTokenCookie);
        response.addCookie(usernameCookie);

        // JSON 응답 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
                "success", true,
                "Authorization", accessToken,
                "refreshToken", refreshToken,
                "username", customUserDetails.getUsername()
        )));
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가능
        cookie.setSecure(true); // HTTPS 환경에서만 동작
        cookie.setPath("/"); // 모든 경로에 대해 쿠키 전송
        cookie.setMaxAge(maxAge); // 쿠키 유효 시간
        return cookie;
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorCode errorCode;

        if (failed instanceof UsernameNotFoundException) {
            errorCode = ErrorCode.USER_NOT_FOUND;
        } else if (failed instanceof BadCredentialsException) {
            errorCode = ErrorCode.BAD_CREDENTIAL;
        } else {
            errorCode = ErrorCode.NOT_BLANK_ID;
        }

        ApiResponse<Object> apiResponse = ApiResponse.failure(errorCode.getMessage(), null);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse);

        response.getWriter().write(json);
        response.getWriter().flush();
    }

    // 토큰 생성 및 응답 로직 재사용 메서드
    public void generateTokenAndRespond(HttpServletResponse response, String userEmail, String role) throws IOException {
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userEmail);
        String refreshToken = jwtTokenProvider.createRefreshToken(userEmail);

        // Authorization 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);

        // ApiResponse에 성공 메시지와 사용자 정보를 포함하여 JSON 본문으로 응답
        Map<String, Object> data = new HashMap<>();
        data.put("userEmail", userEmail);
        data.put("role", role);

        ApiResponse<Object> apiResponse = ApiResponse.success("로그인 성공", data);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}