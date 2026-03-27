package com.mjdku.dkunotifier.scheduler;

import com.mjdku.dkunotifier.crawler.CrawlService;
import com.mjdku.dkunotifier.crawler.PortalLoginService;
import com.mjdku.dkunotifier.domain.Board;
import com.mjdku.dkunotifier.domain.Subscription;
import com.mjdku.dkunotifier.mail.MailService;
import com.mjdku.dkunotifier.model.Post;
import com.mjdku.dkunotifier.repository.BoardRepository;
import com.mjdku.dkunotifier.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrawlScheduler {

    private final PortalLoginService portalLoginService;
    private final CrawlService crawlService;
    private final MailService mailService;
    private final BoardRepository boardRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void crawlAndNotify() throws IOException {
        log.info("스케줄러 시작");

        Map<String, String> cookies = portalLoginService.login();
        List<Board> boards = boardRepository.findAll();

        for(Board board : boards) {
            try {
                // 새 글 감지
                List<Post> newPosts = crawlService.crawlAndDetectNewPosts(board.getPath(), cookies);
                if(newPosts.isEmpty()) continue;

                // 구독자 조회
                List<Subscription> subscriptions = subscriptionRepository.findByBoard(board);
                if(subscriptions.isEmpty()) continue;

                // 구독자에게 메일 발송
                for(Post post : newPosts) {
                    for(Subscription subscription : subscriptions) {
                        mailService.sendNewPostNotification(
                                subscription.getEmail(),
                                board.getName(),
                                post
                        );
                    }
                }
            } catch (IOException e) {
                log.error("게시판 {} 크롤링 중 오류: {}", board.getPath(), e.getMessage());
            }
        }

        log.info("스케줄러 완료");
    }
}
