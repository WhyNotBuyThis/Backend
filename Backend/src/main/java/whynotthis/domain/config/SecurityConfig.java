package whynotthis.domain.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import whynotthis.domain.jwt.JWTFilter;
import whynotthis.domain.jwt.JWTUtil;
import whynotthis.domain.jwt.LoginFilter;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    private final AuthenticationConfiguration authenticationConfiguration;

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

        AuthenticationConfiguration configuration = new AuthenticationConfiguration();

        httpSecurity
                .csrf(csrf -> csrf.disable())// CSRF 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 HTTP 인증 비활성화
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/login", "/", "/user/signup").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class) // 패스워드 검증
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class) // 토큰 검증 로직
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책을 Stateless로 설정
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 지정
                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); // 허용할 헤더 지정
                            corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization")); // 클라이언트에서 읽을 수 있는 헤더
                            corsConfiguration.setMaxAge(3600L);
                            return corsConfiguration;
                        })
                );
                // TODO: 2024/10/31 oauth2 로그인 구현해야함 - Nano
//                .oauth2Login()
//                .successHandler(oAuth)

        return httpSecurity.build();
    }
}