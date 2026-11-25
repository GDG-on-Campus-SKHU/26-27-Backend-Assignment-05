package com.gdg.oauthexample.domain.user.repository;

import com.gdg.oauthexample.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 구글 로그인 실습
    Optional<User> findByGoogleId(String googleId);
}
