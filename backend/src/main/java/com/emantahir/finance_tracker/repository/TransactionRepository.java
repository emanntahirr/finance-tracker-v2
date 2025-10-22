package com.emantahir.finance_tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.emantahir.finance_tracker.model.Portfolio;
import com.emantahir.finance_tracker.model.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolio(Portfolio portfolio);
    List<Transaction> findByPortfolioAndAmountLessThan(Portfolio portfolio, Double amount);

    // query to calculate toalal income of +ve amounts
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.portfolio = :portfolio AND t.amount >0")
    Optional<Double> sumIncomeByPortfolio(Portfolio portfolio);

    // query to calculate total expense of -ve amounts
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.portfolio = :portfolio AND t.amount <0")
    Optional<Double> sumExpenseByPortfolio(Portfolio portfolio);

    @Query("SELECT FUNCTION('DATE_FORMAT', t.date, '%Y-%m') AS month, " +
        "COALESCE(SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END), 0) AS income, " +
        "COALESCE(SUM(CASE WHEN t.amount < 0 THEN ABS(t.amount) ELSE 0 END), 0) AS expense " +
        "FROM Transaction t " +
        "WHERE t.portfolio = :portfolio " +
        "GROUP BY FUNCTION('DATE_FORMAT', t.date, '%Y-%m') " +
        "ORDER BY month")
    List<Object[]> findMonthlyIncomeExpenseSummary(@Param("portfolio") Portfolio portfolio);
}
//empty bag