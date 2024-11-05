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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
            // JSON 요청 본문을 Map으로 변환하여 사용자 이메일과 비밀번호를 추출
            Map<String, String> authRequest = objectMapper.readValue(request.getInputStream(), Map.class);
            String userEmail = authRequest.get("userEmail");
            String userPw = authRequest.get("userPw");

            // 사용자 존재 여부를 사전 확인
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userEmail);
            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found with email: " + userEmail);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, userPw);
            return authenticationManager.authenticate(authToken);
        } catch (UsernameNotFoundException e) {
            // UsernameNotFoundException을 그대로 전달
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userEmail = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.creatJwt(userEmail, role, 60 * 60 * 500L);
        response.addHeader("Authorization", "Bearer " + token);
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

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("errorCode", String.valueOf(errorCode.getCode()));
        jsonResponse.put("message", errorCode.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}