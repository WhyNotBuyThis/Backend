package whynotthis.domain.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import whynotthis.domain.jwt.ExceptionHandlingFilter;
import whynotthis.domain.jwt.JwtFilter;
import whynotthis.domain.jwt.JwtTokenProvider;
import whynotthis.domain.jwt.LoginFilter;
import whynotthis.domain.user.oauth2.LoginSuccessHandler;
import whynotthis.domain.user.repository.UserRepository;
import whynotthis.domain.user.service.CustomOAuth2Service;
import whynotthis.domain.user.service.CustomUserDetailsService;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationEntryPoint entryPoint;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final CustomOAuth2Service customOAuth2Service;
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // 필터 인스턴스 생성
        LoginFilter loginFilter = new LoginFilter(authenticationManager(), jwtTokenProvider, customUserDetailsService,redisTemplate);
        ExceptionHandlingFilter exceptionHandlingFilter = new ExceptionHandlingFilter();

        httpSecurity
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 HTTP 인증 비활성화
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/login", "/", "/user/signup").permitAll() // 인증 필요 없는 URL 설정
                        .requestMatchers("/admin").hasRole("ADMIN") // ADMIN 권한이 필요한 경로
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .oauth2Login(auth -> auth
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2Service))
                        .successHandler(new LoginSuccessHandler(jwtTokenProvider, redisTemplate))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 먼저 실행
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class) // 로그인 필터 추가
                .addFilterBefore(exceptionHandlingFilter, JwtFilter.class) // 예외 처리 필터 추가
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint)) // 인증 실패 시 처리
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless 세션 관리
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // 분리된 CORS 설정 사용

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용 도메인
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용 HTTP 메서드
        corsConfiguration.setAllowedHeaders(List.of("*")); // 허용 요청 헤더
        corsConfiguration.setAllowCredentials(true); // 인증 정보 포함 허용
        corsConfiguration.setMaxAge(3600L); // CORS preflight 요청 캐싱 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}