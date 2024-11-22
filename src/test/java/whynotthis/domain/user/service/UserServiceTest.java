package whynotthis.domain.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;
import whynotthis.domain.jwt.JWTUtil;
import whynotthis.domain.user.dto.UserDTO;
import whynotthis.domain.user.repository.UserRepository;
import whynotthis.domain.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @Test
    public void testSignUpWithDuplicateEmail() {
        // Given: 이미 존재하는 이메일로 첫 번째 회원 가입 시도
        UserDTO user = new UserDTO();
        user.setUserEmail("test@example.com");
        user.setUserPw("password123");

        // UserRepository Mock 설정 - 이메일 중복 시 true 반환
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(true);

        // Then: GeneralException이 발생하는지 확인
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            userService.signUp(user);
        });

        // 발생한 에러 코드가 예상과 일치하는지 확인
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }
}