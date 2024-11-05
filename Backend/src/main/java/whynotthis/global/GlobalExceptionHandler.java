package whynotthis.global;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import whynotthis.domain.exception.ErrorResponseDto;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(GeneralException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(ErrorCode errorCode) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
}