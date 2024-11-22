package whynotthis.domain.jwt;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorCode {

    // User 회원가입 (400 - Bad Request)
    EMAIL_ALREADY_EXISTS(400, "이미 존재하는 이메일입니다."),
    NOT_BLANK_ID(400, "이메일을 입력해주세요."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),

    // 로그인 (401 - Unauthorized)
    BAD_CREDENTIAL(401, "아이디 또는 비밀번호를 확인해주세요."),
    USER_NOT_FOUND(401, "존재하지 않는 사용자입니다."),

    // 기타 오류 (500 - Internal Server Error)
    GENERAL_FILTER(500, "서버 내부에서 오류가 발생했습니다."),

    // 유효성 검사 오류 (400 - Bad Request)
    NOT_VALID_FORMAT(400, "유효하지 않은 이메일 형식입니다."),
    NOT_VALID_PWD(400, "비밀번호는 8자 이상 20자 이하로 입력해주세요."),

    // 아이템 서치 오류 (404 - Not Found)
    NOT_FOUND_ITEM(404, "요청한 아이템을 찾을 수 없습니다."),

    // 게시글 관련 오류
    EMPTY_TITLE(400, "제목을 입력해주세요."),
    EMPTY_CONTENT(400, "내용을 입력해주세요."),
    NOT_FIND_BOARD(404, "존재하지 않는 게시글입니다."),
    UNAUTHORIZED_ACCESS(403, "권한이 없습니다.");

    private final int code; // HTTP 상태 코드
    private final String message; // 사용자에게 전달할 메시지

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 에러 메시지를 기반으로 적절한 ErrorCode를 반환하는 메서드
     *
     * @param errorMessage 예외 메시지
     * @return 매칭되는 ErrorCode, 없으면 기본값으로 NOT_FOUND_ITEM 반환
     */
    public static ErrorCode fromMessage(String errorMessage) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorMessage.contains(errorCode.message))
                .findFirst()
                .orElse(NOT_FOUND_ITEM); // 기본값 설정
    }
}