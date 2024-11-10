package whynotthis.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomUserDetailsService userDetailsService;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        super.setFilterProcessesUrl("/user/login");
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
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

        String userEmail = customUserDetails.getUsername();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        // JWT 토큰 생성
        String token = jwtUtil.creatJwt(userEmail, role, 60 * 60 * 500L);

        // Authorization 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);

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
}