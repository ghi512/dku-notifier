package com.mjdku.dkunotifier.controller;

import com.mjdku.dkunotifier.domain.Subscription;
import com.mjdku.dkunotifier.repository.BoardRepository;
import com.mjdku.dkunotifier.repository.SubscriptionRepository;
import com.mjdku.dkunotifier.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final BoardRepository boardRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VerificationService verificationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("boards", boardRepository.findAll());
        return "index";
    }

    @PostMapping("/send-code")
    @ResponseBody
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        if(email.isBlank()) {
            return ResponseEntity.badRequest().body("이메일을 입력해주세요.");
        }
        verificationService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/verify-code")
    @ResponseBody
    public ResponseEntity<String> verifyCode(@RequestParam String email,
                                             @RequestParam String code) {
        boolean result = verificationService.verify(email, code);
        if(!result) {
            return ResponseEntity.badRequest().body("인증번호가 올바르지 않거나 만료되었습니다.");
        }
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping("/subscribe")
    @ResponseBody
    public ResponseEntity<String> subscribe(
            @RequestParam String email,
            @RequestParam List<Long> boardIds
    ) {
        if(email.isBlank() || boardIds.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일과 게시판을 선택해주세요.");
        }

        if(!verificationService.ifVerified(email)) {
            return ResponseEntity.badRequest().body("이메일 인증이 필요합니다.");
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
