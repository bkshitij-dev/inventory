package com.app.inventory.repository;

import com.app.inventory.enums.TokenType;
import com.app.inventory.model.ExternalAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalAccessTokenRepository extends JpaRepository<ExternalAccessToken, Long> {

    @Query(value = "SELECT eat.* FROM external_access_tokens eat WHERE eat.token = ?1 AND eat.active = true",
            nativeQuery = true)
    Optional<ExternalAccessToken> findActiveToken(String token);

    @Query(value = "SELECT eat.* FROM external_access_tokens eat WHERE eat.token = ?1", nativeQuery = true)
    Optional<ExternalAccessToken> findByToken(String token);

    @Query(value = "SELECT eat.* FROM external_access_tokens eat WHERE eat.user_id = ?1 AND eat.token_type = ?2 " +
            "AND eat.active = true", nativeQuery = true)
    Optional<ExternalAccessToken> findByUserAndTokenType(Long userId, TokenType tokenType);

}
