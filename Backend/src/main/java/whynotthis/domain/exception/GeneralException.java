package whynotthis.domain.exception;

import lombok.Getter;
import whynotthis.domain.jwt.ErrorCode;

@Getter
public class GeneralException extends RuntimeException{

    private final ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
