package whynotthis.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(GeneralException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(ErrorCode errorCode) {
        // ApiResponse 형식으로 에러 응답 생성
        ApiResponse<Object> errorResponse = ApiResponse.failure(errorCode.getMessage(), null);
        HttpStatus status = HttpStatus.valueOf(errorCode.getCode());

        return new ResponseEntity<>(errorResponse, status);
    }
}