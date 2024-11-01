package whynotthis.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import whynotthis.domain.user.dto.UserDTO;
import whynotthis.domain.user.service.UserService;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserDTO userDTO) {
        userService.signUp(userDTO);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        String token = userService.login(userDTO);
        System.out.println("토큰 생성완료");
        return ResponseEntity.ok(token); // 토큰을 JSON 형식으로 반환
    }
}