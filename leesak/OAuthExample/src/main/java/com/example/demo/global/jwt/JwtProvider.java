package com.example.demo.global.jwt;

import com.example.demo.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    public String createAccessToken(Long userId, Role role) {
        return createToken(userId, ACCESS, accessTokenExpiration, role);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH, refreshTokenExpiration, null);
    }

    private String createToken(Long userId, String type, long expiration, Role role) {
        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", type);

        if (role != null) {
            builder.claim("role", role.name());
        }

        return builder
                .id(UUID.randomUUID().toString())                 // 같은 순간에 만들어도 토큰이 겹치지 않게
                .issuedAt(now)                                    // iat: 발급 시각
                .expiration(new Date(now.getTime() + expiration)) // exp: 만료 시각
                .signWith(secretKey, Jwts.SIG.HS256)              // 비밀키로 서명 (HS256)
                .compact();
    }

    // 토큰 검증하기
    // 서명이 틀리거나, 만료됐거나, 종류가 다르면 JwtException이 발생합니다.
    public AccessTokenInfo parseAccessToken(String token) {
        Claims claims = getClaims(token, ACCESS);
        Long userId = Long.valueOf(claims.getSubject());
        String roleName = claims.get("role", String.class);

        if (roleName == null) {
            throw new JwtException("토큰에 역할 정보가 없습니다.");
        }

        return new AccessTokenInfo(userId, Role.valueOf(roleName));
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = getClaims(token, REFRESH);
        return Long.valueOf(claims.getSubject());
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

    public record AccessTokenInfo(Long userId, Role role) {}
}
