package com.example.demo.domain.user.repository;

import java.util.Optional;

import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 구글 로그인 실습
    Optional<User> findByGoogleId(String googleId);
}
