package whynotthis.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import whynotthis.domain.user.entity.UserEntity;

@Getter
@Setter
public class UserDTO {
    @NotBlank(message="이메일을 입력 해주세요")
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    private String userEmail;

    @NotBlank(message="나이를 입력해주세요")
    private int userAge;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String userPw;

    private String role;

    public UserEntity toEntity() {
        return new UserEntity(
                this.getUserEmail(),
                this.getUserAge(),
                this.getUserPw()
        );
    }

}
