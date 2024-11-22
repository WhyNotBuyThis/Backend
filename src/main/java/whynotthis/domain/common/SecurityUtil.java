package whynotthis.domain.common;

import org.springframework.security.core.context.SecurityContextHolder;
import whynotthis.domain.user.dto.CustomUserDetails;

public class SecurityUtil {

    private SecurityUtil() {

    }

    public static CustomUserDetails getEmail() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
