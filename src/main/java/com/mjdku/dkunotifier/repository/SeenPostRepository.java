package com.mjdku.dkunotifier.repository;

import com.mjdku.dkunotifier.domain.Board;
import com.mjdku.dkunotifier.domain.SeenPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeenPostRepository extends JpaRepository<SeenPost, Long> {
    boolean existsByBoardAndPostSeq(Board board, String postSeq);
    List<SeenPost> findByBoard(Board board);
}
