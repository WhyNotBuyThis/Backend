package whynotthis.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;
import whynotthis.domain.jwt.JWTUtil;
import whynotthis.domain.user.dto.UserDTO;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

        // 회원가입 로직
        public void signUp(UserDTO userDTO) {

            String userEmail = userDTO.getUserEmail();
            String userPw = userDTO.getUserPw();

            if (checkEmailDuplicate(userEmail)) {
                System.out.println(userEmail);
                throw new GeneralException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            userDTO.setUserPw(bCryptPasswordEncoder.encode(userPw));
            UserEntity user = userDTO.toEntity();
            userRepository.save(user);
        }

        // 이메일 중복 체크
        private boolean checkEmailDuplicate(String userEmail) {
            return userRepository.existsByUserEmail(userEmail);
        }

}
