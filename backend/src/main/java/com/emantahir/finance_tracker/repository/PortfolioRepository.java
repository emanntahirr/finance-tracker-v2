package com.emantahir.finance_tracker.repository;
import java.util.List;
import java.util.Optional;
import com.emantahir.finance_tracker.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emantahir.finance_tracker.model.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);

    Optional<Portfolio> findByUser(User user);
}