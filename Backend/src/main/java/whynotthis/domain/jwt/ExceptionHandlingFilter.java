package whynotthis.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;

import java.io.IOException;

public class ExceptionHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException e) {
            ErrorCode errorCode = ErrorCode.fromMessage(e.getMessage());
            setErrorResponse(response, errorCode);
        } catch (GeneralException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized");
                return;
            }
            setErrorResponse(response, ErrorCode.GENERAL_FILTER);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse를 사용하여 에러 응답 구성
        ApiResponse<Object> apiResponse = ApiResponse.failure(errorCode.getMessage(), null);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}