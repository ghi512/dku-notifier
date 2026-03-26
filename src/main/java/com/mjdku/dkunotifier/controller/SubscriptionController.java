package com.mjdku.dkunotifier.controller;

import com.mjdku.dkunotifier.domain.Subscription;
import com.mjdku.dkunotifier.repository.BoardRepository;
import com.mjdku.dkunotifier.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final BoardRepository boardRepository;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("boards", boardRepository.findAll());
        return "index";
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(
            @RequestParam String email,
            @RequestParam List<Long> boardIds
    ) {
        if(email.isBlank() || boardIds.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일과 게시판을 선택해주세요.");
        }

        for (Long boardId : boardIds) {
            boardRepository.findById(boardId).ifPresent(board -> {
                boolean exists = subscriptionRepository.existsByEmailAndBoard(email, board);
                if(!exists) {
                    subscriptionRepository.save(
                            Subscription.builder()
                                    .email(email)
                                    .board(board)
                                    .build()
                    );
                }
            });
        }

        return ResponseEntity.ok("구독이 완료되었습니다.");
    }
}
