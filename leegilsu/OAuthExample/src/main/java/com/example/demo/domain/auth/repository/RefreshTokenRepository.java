package com.example.demo.domain.auth.repository;

import java.util.Optional;

import com.example.demo.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);
}
