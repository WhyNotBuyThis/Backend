package whynotthis.domain.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.domain.user.repository.UserRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // 인증이 필요하지 않은 경로는 필터를 통과
        if (request.getRequestURI().startsWith("/user/login") || request.getRequestURI().startsWith("/user/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            throw new GeneralException(ErrorCode.TOKEN_EXPIRED);
        }

        String userEmail = jwtUtil.getUserEmail(token);

        UserEntity user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        filterChain.doFilter(request, response);
    }
}