package whynotthis.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.user.dto.UserDTO;
import whynotthis.domain.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<ApiResponse<Object>> signUp(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류 메시지 수집
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            // ApiResponse 형식으로 실패 응답 반환
            ApiResponse<Object> response = ApiResponse.failure("유효성 검사 실패", errorMessages);
            return ResponseEntity.badRequest().body(response);
        }

        // 회원가입 성공 로직
        userService.signUp(userDTO);

        // 성공 응답 생성
        ApiResponse<Object> response = ApiResponse.success("회원가입 완료 하였습니다.", userDTO.getUserEmail());
        return ResponseEntity.ok(response);
    }
}