package com.example.oauthexample.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private String sessionId;

    public RefreshToken(Long userId, String token, String sessionId) {
        this.userId = userId;
        this.token = token;
        this.sessionId = sessionId;
    }

    // 재발급할 때마다 새 토큰으로 덮어씀 (Rotation)
    public void updateToken(String token) {
        this.token = token;
    }
}
