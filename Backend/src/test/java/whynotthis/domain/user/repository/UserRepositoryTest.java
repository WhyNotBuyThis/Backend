package whynotthis.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import whynotthis.domain.user.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@DisplayName("회원 레포 테스트")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 저장")
    void save() {
        // Given
    	UserEntity user = new UserEntity("asdasd@naver.com",23,"asdasd");
        // When
        userRepository.save(user);
        // Then
    	userRepository.findByUserEmail(user.getUserEmail());

    }



}