package com.mjdku.dkunotifier.crawler;

import com.mjdku.dkunotifier.domain.Board;
import com.mjdku.dkunotifier.domain.SeenPost;
import com.mjdku.dkunotifier.model.Post;
import com.mjdku.dkunotifier.repository.BoardRepository;
import com.mjdku.dkunotifier.repository.SeenPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlService {

    private final BoardCrawlerService boardCrawlerService;
    private final BoardRepository boardRepository;
    private final SeenPostRepository seenPostRepository;

    @Transactional
    public List<Post> crawlAndDetectNewPosts(String boardPath) throws IOException {
        // DB에서 게시판 정보 조회
        Board board = boardRepository.findByPath(boardPath)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 게시판: " + boardPath));

        // 크롤링
        List<Post> posts = boardCrawlerService.crawl(boardPath);

        // 새 글 필터링
        Set<String> seenPostSeqs = seenPostRepository.findByBoard(board)
                .stream()
                .map(SeenPost::getPostSeq)
                .collect(Collectors.toSet());

        List<Post> newPosts = posts.stream()
                .filter(post -> !seenPostSeqs.contains(post.getPostSeq()))
                .toList();

        // 새 글 SeenPost에 저장
        newPosts.forEach(post -> seenPostRepository.save(
                SeenPost.builder()
                        .board(board)
                        .postSeq(post.getPostSeq())
                        .build()
        ));

        log.info("게시판 {} - 전체 {}개 중 새 글 {}개 감지", boardPath, posts.size(), newPosts.size());
        return newPosts;
    }
}
