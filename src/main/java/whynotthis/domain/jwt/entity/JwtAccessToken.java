package whynotthis.domain.jwt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "JwtAccessToken", timeToLive = 60 * 10)
@RequiredArgsConstructor
public class JwtAccessToken {

    @Id
    private final String username;
    private final String accessToken;
}
