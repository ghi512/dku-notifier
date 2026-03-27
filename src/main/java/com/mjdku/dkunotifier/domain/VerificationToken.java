package com.mjdku.dkunotifier.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token; // 인증번호 6자리
    private LocalDateTime expiredAt;
    private boolean verified;

    @Builder
    public VerificationToken(String email, String token) {
        this.email = email;
        this.token = token;
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
        this.verified = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void verify() {
        this.verified = true;
    }
}
