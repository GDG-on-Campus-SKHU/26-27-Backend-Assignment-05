package com.group.oauth.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    private static final long ACCESS_EXPIRATION = 1000L * 60 * 30;
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, "access", ACCESS_EXPIRATION);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, "refresh", REFRESH_EXPIRATION);
    }

    private String createToken(Long userId, String type, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }
    public Long getUserIdFromAccessToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String type = claims.get("type", String.class);

        if (!"access".equals(type)) {
            throw new IllegalArgumentException("Access Token이 아닙니다.");
        }

        return Long.valueOf(claims.getSubject());
    }
}
