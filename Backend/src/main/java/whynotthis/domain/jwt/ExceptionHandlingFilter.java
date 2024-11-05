package whynotthis.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import whynotthis.domain.exception.GeneralException;

import java.io.IOException;

public class ExceptionHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException e){
            ErrorCode errorCode = ErrorCode.fromMessage(e.getMessage());
            setErrorResponse(response, errorCode);
        } catch (GeneralException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            // 인증 관련 예외만 처리
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized");
                return;
            }
            // 기타 예외 처리
            setErrorResponse(response, ErrorCode.GENERAL_FILTER);
        }

    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public static class ErrorResponse {
        private final int code;
        private final String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}