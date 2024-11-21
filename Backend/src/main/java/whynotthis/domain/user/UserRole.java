package whynotthis.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_ADMIN("관리자"), ROLE_USER("일반 사용자");

    private final String role;
}
