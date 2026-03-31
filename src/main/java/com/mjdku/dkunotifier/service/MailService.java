package com.mjdku.dkunotifier.service;

import com.mjdku.dkunotifier.dto.Post;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from}")
    private String fromEmail;

    public void sendNewPostNotification(
            String toEmail,
            String boardName,
            Post post
    ) {
        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(toEmail))
                    .subject("[단국대 알림] " + boardName + " - " + post.getTitle())
                    .text("새 글이 올라왔어요!\n\n" +
                            "게시판: " + boardName + "\n" +
                            "제목: " + post.getTitle() + "\n" +
                            "작성자: " + post.getAuthor() + "\n" +
                            "날짜: " + post.getDate() + "\n\n" +
                            "바로가기: " + post.getUrl())
                    .build();

            resend.emails().send(request);
            log.info("메일 발송 완료 → {}", toEmail);
        } catch (Exception e) {
            log.info("메일 발송 실패 → {} : {}", toEmail, e.getMessage());
        }
    }

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(toEmail))
                    .subject("[단국대 알림] 이메일 인증번호")
                    .text("안녕하세요!\n\n" +
                                    "인증번호: " + code + "\n\n" +
                                    "5분 이내에 입력해주세요.")
                            .build();

            resend.emails().send(request);
            log.info("인증 메일 발송 완료 → {}", toEmail);
        } catch (Exception e) {
            log.error("인증 메일 발송 실패 → {} : {}", toEmail, e.getMessage());
        }
    }
}
