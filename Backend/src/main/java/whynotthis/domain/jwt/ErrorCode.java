package whynotthis.domain.jwt;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorCode {


    // User 회원가입
    EMAIL_ALREADY_EXISTS(406, "이미 있는 이메일 입니다."),
    NOT_BLANK_ID(407, "이메일을 입력해주세요"),
    TOKEN_EXPIRED(408, "토큰이 만료되었습니다."),

    // 로그인
    BAD_CREDENTIAL(501, "아이디 또는 비밀번호를 확인해주세요."),
    USER_NOT_FOUND(502, "존재하지 않는 사용자입니다."),

    // 기타 오류
    GENERAL_FILTER(601, "필터에서 오류가 발생했습니다."),

    // 유효성 검사 오류
    NOT_VALID_FORMAT(700, "올바른 이메일 형식을 입력해주세요."),
    NOT_VALID_PWD(701, "비밀번호는 8자 이상 20자 이하로 입력해주세요."),

    // 아이템 서치 오류
    NOT_FOUND_ITEM(801, "아이템을 찾을 수 없습니다."),

    EMPTY_TITLE(450,"제목을 입력해주세요" ),
    EMPTY_CONTENT(451,"내용을 입력해주세요" ),
    NOT_FIND_BOARD(452,"존재하지 않는 게시글입니다." );

    private final int code;
    private final String message;


    ErrorCode(int code,String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 에러 메시지를 기반으로 적절한 ErrorCode를 반환하는 메서드
     * @param errorMessage 예외 메시지
     * @return 매칭되는 ErrorCode, 없으면 기본값으로 NOT_BLANK_ID 반환
     */

    public static ErrorCode fromMessage(String errorMessage) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorMessage.contains(errorCode.message))
                .findFirst()
                .orElse(NOT_FOUND_ITEM); // 기본값 설정
    }
}
