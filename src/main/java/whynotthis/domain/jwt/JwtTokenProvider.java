package whynotthis.domain.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String accessSecretKey;
    private final String refreshSecretKey;
    private final String issuer;

    private final long accessTokenValidTime = 10 * 60 * 1000L;    // 액세스 토큰의 유효기간은 10분
    private final long refreshTokenValidTime = 10 * 24 * 60 * 60 * 1000L;    // 리프레시 토큰의 유효기간은 10일

    public JwtTokenProvider(
            @Value("${jwt.ACCESS_SECRET_KEY}") String accessSecretKey,
            @Value("${jwt.REFRESH_SECRET_KEY}") String refreshSecretKey,
            @Value("${jwt.ISSUER}") String issuer) {

        this.accessSecretKey = accessSecretKey;
        this.refreshSecretKey = refreshSecretKey;
        this.issuer = issuer;
    }


    public String createAccessToken(String username) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    public String createRefreshToken(String username) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }


    public String getUserPkFromRefreshToken(String refreshToken) {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();
    }
}
