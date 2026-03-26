package com.mjdku.dkunotifier.crawler;

import com.mjdku.dkunotifier.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardCrawlerService {

    private static final String BASE_URL = "https://portal.dankook.ac.kr";

    public List<Post> crawl(String boardPath, Map<String, String> cookies) throws IOException {
        // 게시판 접근
        Document doc = Jsoup.connect(BASE_URL + boardPath)
                .cookies(cookies)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(10000)
                .get();

        // 글 목록 파싱
        List<Post> posts = new ArrayList<>();
        Elements rows = doc.select("table#boardTypeList tbody tr");

        for(Element row : rows) {
            String postSeq = row.select("td.bc-s-post_seq").text().trim();
            String title = row.select("td.bc-s-title span[title]").attr("title").trim();
            String author = row.select("td.bc-s-cre_user_name").text().trim();
            String date = row.select("td.bc-s-cre_dt").text().trim();
            String url = BASE_URL + row.attr("data-url");

            if(!postSeq.isEmpty()) {
                posts.add(new Post(postSeq, title, author, date, url));
            }
        }

        log.info("게시판 {} 크롤링 완료 - {}개 글 수집", boardPath, posts.size());
        return posts;
    }

}
