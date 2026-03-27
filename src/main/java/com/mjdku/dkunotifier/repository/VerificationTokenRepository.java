package com.mjdku.dkunotifier.repository;

import com.mjdku.dkunotifier.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByEmailAndToken(String email, String token);
    Optional<VerificationToken> findTopByEmailOrderByExpiredAtDesc(String email);
}
