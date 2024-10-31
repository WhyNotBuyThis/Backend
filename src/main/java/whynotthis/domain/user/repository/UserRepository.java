package whynotthis.domain.user.repository;

import whynotthis.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserEmail(String userEmail);

    UserEntity findByUserEmail(String userEmail);
}
