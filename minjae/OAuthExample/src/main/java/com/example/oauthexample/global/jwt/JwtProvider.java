package com.example.oauthexample.global.jwt;

import com.example.oauthexample.domain.role.entity.Permission;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                       @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long userId, Set<Permission> permissions) {
        return createToken(userId, permissions, ACCESS, accessTokenExpiration);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null, REFRESH, refreshTokenExpiration);
    }

    private String createToken(Long userId, Set<Permission> permissions, String type, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(userId))                  // sub: 누구의 토큰인지
                .claim("type", type)                           // access인지 refresh인지
                .claim("permissions", permissions)
                .id(UUID.randomUUID().toString())                 // 같은 순간에 만들어도 토큰이 겹치지 않게
                .issuedAt(now)                                    // iat: 발급 시각
                .expiration(new Date(now.getTime() + expiration)) // exp: 만료 시각
                .signWith(secretKey, Jwts.SIG.HS256)              // 비밀키로 서명 (HS256)
                .compact();
    }

    public Claims getClaimsFromAccessToken(String token) {
        return getClaims(token, ACCESS);
    }

    public Claims getClaimsFromRefreshToken(String token) {
        return getClaims(token, REFRESH);
    }

    private Claims getClaims(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!expectedType.equals(claims.get("type", String.class))) {
            throw new JwtException("토큰 종류가 올바르지 않습니다.");
        }

        return claims;
    }
}
