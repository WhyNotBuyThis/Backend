package whynotthis.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

            boolean isExist = checkEmailDuplicate(userEmail);

            if (isExist) {
                throw new UsernameNotFoundException ("이미 존재하는 이메일입니다.");
            } else {
                userDTO.setUserPw(bCryptPasswordEncoder.encode(userPw));
                UserEntity user = userDTO.toEntity();
                userRepository.save(user);
            }
        }

        public String login(UserDTO userDTO) {
            UserEntity user = userRepository.findByUserEmail(userDTO.getUserEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다."));

            if (!bCryptPasswordEncoder.matches(userDTO.getUserPw(),user.getUserPw())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            return jwtUtil.creatJwt(user.getUserEmail(), userDTO.getRole(), 60 * 60 * 1000L);
        }

        // 이메일 중복 체크
        private boolean checkEmailDuplicate(String userEmail) {
            return userRepository.existsByUserEmail(userEmail);
        }

    // 로그인






}
