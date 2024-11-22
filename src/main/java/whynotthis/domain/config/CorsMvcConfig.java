package whynotthis.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 허용할 도메인 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
                .allowedHeaders("*") // 모든 헤더 허용
                .exposedHeaders("Authorization") // 노출할 헤더 설정
                .allowCredentials(true); // 인증 정보를 포함한 요청 허용
    }
}
