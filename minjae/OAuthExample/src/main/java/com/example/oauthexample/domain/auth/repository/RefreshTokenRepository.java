package com.example.oauthexample.domain.auth.repository;

import java.util.Optional;

import com.example.oauthexample.domain.auth.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findBySessionId(String sessionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RefreshToken r where r.sessionId = :sessionId")
    Optional<RefreshToken> findBySessionIdForUpdate(
            @Param("sessionId") String sessionId
    );
}
