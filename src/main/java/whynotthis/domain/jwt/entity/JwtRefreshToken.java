package whynotthis.domain.jwt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RequiredArgsConstructor
@RedisHash(value = "JwtRefreshToken", timeToLive = 60 * 60 * 24 * 10)
public class JwtRefreshToken {

    @Id
    private final String username;
    private final String refreshToken;
}
