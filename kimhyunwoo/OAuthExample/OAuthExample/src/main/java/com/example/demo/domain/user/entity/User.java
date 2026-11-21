package com.example.demo.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // 구글 로그인 실습: 같은 구글 계정은 고유 ID로 찾습니다.
    @Column(unique = true)
    private String googleId;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User withGoogleAccount(String email, String googleId, String passwordHash) {
        User user = new User(email, passwordHash);
        user.googleId = googleId;
        return user;
    }
}
