package com.mjdku.dkunotifier.repository;

import com.mjdku.dkunotifier.domain.Board;
import com.mjdku.dkunotifier.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByBoard(Board board);
}
