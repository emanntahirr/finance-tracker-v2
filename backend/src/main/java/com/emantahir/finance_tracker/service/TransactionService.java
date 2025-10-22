package com.emantahir.finance_tracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.emantahir.finance_tracker.model.Portfolio;
import com.emantahir.finance_tracker.model.Transaction;
import com.emantahir.finance_tracker.model.User;
import com.emantahir.finance_tracker.repository.PortfolioRepository;
import com.emantahir.finance_tracker.repository.TransactionRepository;
import com.emantahir.finance_tracker.repository.UserRepository;
import com.emantahir.finance_tracker.util.SecurityUtil;


@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtil securityUtil;


    public TransactionService(TransactionRepository repository,
        UserRepository userRepository,
        PortfolioRepository portfolioRepository,
        SecurityUtil securityUtil)
    {
        this.repository = repository;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.securityUtil = securityUtil;
    }

private List<Transaction> getAuthenticatedUserTransactions() {
    
    String username = securityUtil.getCurrentUserUsername();

    User currentUser = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new RuntimeException("User not found: " + username));

    Portfolio portfolio = portfolioRepository.findByUser(currentUser)
        .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + username));

    return repository.findByPortfolio(portfolio);
}

    public List<Transaction> getAllTransactions() {
        
        String username = securityUtil.getCurrentUserUsername();

        User currentUser = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolio = portfolioRepository.findByUser(currentUser)
            .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        return getAuthenticatedUserTransactions();
    }

    private Portfolio getCurrentUserPortfolio() {
    String username = securityUtil.getCurrentUserUsername();

    User currentUser = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new RuntimeException("User not found: " + username));

    Portfolio userPortfolio = portfolioRepository.findByUser(currentUser)
        .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + username));
        
    return userPortfolio;
}

public Transaction addTransaction(Transaction transaction) {
    
    Portfolio userPortfolio = getCurrentUserPortfolio();
    
    transaction.setPortfolio(userPortfolio);
    
    return repository.save(transaction);
}

    public double getBalance() {
        Portfolio portfolio = getCurrentUserPortfolio();

        double income = repository.sumIncomeByPortfolio(portfolio).orElse(0.0);

        double expenseRaw = repository.sumExpenseByPortfolio(portfolio).orElse(0.0);

        return income + expenseRaw;
    }

    public Map<String, Double> getIncomeExpenseSummary() {

        Portfolio portfolio = getCurrentUserPortfolio();

        double income = repository.sumIncomeByPortfolio(portfolio).orElse(0.0);
        double expenseRaw = repository.sumExpenseByPortfolio(portfolio).orElse(0.0);

        double expense = Math.abs(expenseRaw);
        double balance = income + expenseRaw; // income + -ve expense

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expenses", expense);
        summary.put("balance", balance);

        return summary;
    }

    public List<Transaction> getIncomeTransactions() {
        List<Transaction> allTransactions = getAuthenticatedUserTransactions();
        List<Transaction> income = new ArrayList<>();

        for (Transaction t : allTransactions) {
            if (t.getAmount() > 0) {
                income.add(t);
            }
        }
        return income;
    }

    public List<Map<String, Object>> getMonthlySummaryForChart() {
        Portfolio portfolio = getCurrentUserPortfolio();

        List<Object[]> rawData = repository.findMonthlyIncomeExpenseSummary(portfolio);
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] row : rawData) {
            String month = (String) row[0];
            Double income = (Double) row[1];
            Double expenseRaw = (Double) row[2];
            
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("month", month);
            // Expense is returned as negative from the query, convert to positive for chart
            monthData.put("income", income != null ? income : 0.0);
            monthData.put("expense", expenseRaw != null ? Math.abs(expenseRaw) : 0.0);
            
            chartData.add(monthData);
        }

        return chartData;
    }
}