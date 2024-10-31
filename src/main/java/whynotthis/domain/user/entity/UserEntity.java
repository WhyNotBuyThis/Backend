package whynotthis.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whynotthis.domain.user.UserRole;
import whynotthis.global.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private int userAge;
    private String userPw;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(String userEmail, int userAge, String userPw) {
        this.userEmail = userEmail;
        this.userAge = userAge;
        this.userPw = userPw;
        this.role = UserRole.ROLE_USER;
    }
}
