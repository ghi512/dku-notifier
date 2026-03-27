package com.mjdku.dkunotifier.service;

import com.mjdku.dkunotifier.domain.VerificationToken;
import com.mjdku.dkunotifier.mail.MailService;
import com.mjdku.dkunotifier.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        verificationTokenRepository.findTopByEmailOrderByExpiredAtDesc(email)
                .ifPresent(verificationTokenRepository::delete);

        verificationTokenRepository.save(
                VerificationToken.builder()
                        .email(email)
                        .token(code)
                        .build()
        );

        mailService.sendVerificationEmail(email, code);
        log.info("인증번호 발송 완료 → {}", email);
    }

    public boolean verify(String email, String code) {
        return verificationTokenRepository.findByEmailAndToken(email, code)
                .map(token -> {
                    if(token.isExpired()) {
                        log.warn("인증번호 만료 → {}", email);
                        return false;
                    }
                    token.verify();
                    verificationTokenRepository.save(token);
                    return true;
                })
                .orElse(false);
    }

    public boolean ifVerified(String email) {
        return verificationTokenRepository.findTopByEmailOrderByExpiredAtDesc(email)
                .map(token -> token.isVerified() && !token.isExpired())
                .orElse(false);
    }
}
