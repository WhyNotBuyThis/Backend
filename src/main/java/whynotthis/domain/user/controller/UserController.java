package whynotthis.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import whynotthis.domain.user.dto.UserDTO;
import whynotthis.domain.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/signup")
    public String singUp(UserDTO userDTO) {
        userService.signUp(userDTO);
        return "index";
    }

//    @PostMapping("/user/login")
//    public String login(@RequestBody UserDTO userDTO) {
//
//        return "index";
//    }

}
