package whynotthis.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whynotthis.domain.user.UserRole;
import whynotthis.global.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userEmail;
    private String userPw;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(String userEmail, String userPw) {
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.role = UserRole.ROLE_USER;
    }
}
