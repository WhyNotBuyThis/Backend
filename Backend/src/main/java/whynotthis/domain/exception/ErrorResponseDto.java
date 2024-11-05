package whynotthis.domain.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@RequiredArgsConstructor
public class ErrorResponseDto {
    private final int code;
    private final String message;
    private List<ValidationError> validErrors;

    @Data
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String filed, String message) {
        if(Objects.isNull(validErrors)) {
            validErrors = new ArrayList<>();
        }
        validErrors.add(new ValidationError(filed,message));
    }


}
