package com.emantahir.finance_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emantahir.finance_tracker.model.Investment;
import com.emantahir.finance_tracker.model.Portfolio;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByPortfolio(Portfolio portfolio);
}
