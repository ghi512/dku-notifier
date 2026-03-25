package com.mjdku.dkunotifier.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortalLoginService {

    private static final String LOGIN_URL =
            "https://portal.dankook.ac.kr/proc/Login.eps";
    private static final String BOARD_URL =
            "https://portal.dankook.ac.kr/p/CTT016";

    @Value("${portal.id}")
    private String portalId;

    @Value("${portal.password}")
    private String portalPassword;

    public Map<String, String> login() throws IOException {
        Connection.Response response = Jsoup.connect(LOGIN_URL)
                .method(Connection.Method.POST)
                .data("user_id", portalId)
                .data("user_password", portalPassword)
                .data("auto_login", "N")
                .data("returnurl", "https://portal.dankook.ac.kr")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .followRedirects(true)
                .timeout(10000)
                .execute();

        Map<String, String> cookies = response.cookies();

        // 실제 게시판 접근해서 진짜 로그인됐는지 확인
        Connection.Response boardResponse = Jsoup.connect(BOARD_URL)
                .cookies(cookies)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .followRedirects(true)
                .timeout(10000)
                .execute();

        // URL 확인
        log.debug("최종 도달 URL: {}", boardResponse.url().toString());

        // HTML에 실제 게시판 내용이 있는지 확인
        Document doc = boardResponse.parse();
        String bodyText = doc.body().text();
        log.debug("페이지 내용 일부: {}", bodyText.substring(0, Math.min(200, bodyText.length())));

        if (boardResponse.url().toString().toLowerCase().contains("login")) {
            throw new IllegalStateException("포털 로그인 실패 - 게시판 접근 불가");
        }

        log.info("포털 로그인 성공");
        return cookies;
    }

}
