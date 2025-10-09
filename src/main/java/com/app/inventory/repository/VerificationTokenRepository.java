package com.app.inventory.repository;

import com.app.inventory.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query(value = "SELECT vt.* FROM verification_tokens vt WHERE vt.token = ?1 AND vt.active = true", nativeQuery = true)
    Optional<VerificationToken> findByToken(String token);

    @Query(value = "SELECT vt.* FROM verification_tokens vt WHERE vt.user_id = ?1 AND vt.active = true", nativeQuery = true)
    Optional<VerificationToken> findByUser(Long userId);

}
