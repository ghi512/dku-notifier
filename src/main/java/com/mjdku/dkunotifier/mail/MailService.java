package com.mjdku.dkunotifier.mail;

import com.mjdku.dkunotifier.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendNewPostNotification(
            String toEmail,
            String boardName,
            Post post
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[단국대 알림] " + boardName + " - " + post.getTitle());
            message.setText("새 글이 올라왔어요!\n\n" +
                            "게시판: " + boardName + "\n" +
                            "제목: " + post.getTitle() + "\n" +
                            "작성자: " + post.getAuthor() + "\n" +
                            "날짜: " + post.getDate() + "\n\n" +
                            "바로가기: " + post.getUrl()
            );
            mailSender.send(message);
            log.info("메일 발송 완료 → {}", toEmail);
        } catch (Exception e) {
            log.info("메일 발송 실패 → {} : {}", toEmail, e.getMessage());
        }
    }

}
