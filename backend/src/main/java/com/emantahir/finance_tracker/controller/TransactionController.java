package com.emantahir.finance_tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emantahir.finance_tracker.model.Transaction;
import com.emantahir.finance_tracker.service.TransactionService;
import com.emantahir.finance_tracker.util.SecurityUtil;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    
    public TransactionController(TransactionService service) {
        this.service = service;

    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return service.getAllTransactions();
    }

    /**
     * centralised to prevent duplicate calculations across multiple frontend pages
     */
    @GetMapping("/balance")
    public double getBalance() {
        return service.getBalance();
    }

    @GetMapping("/summary")
    public Map<String, Double> getIncomeExpenseSummary() {
        return service.getIncomeExpenseSummary();
    }

    @GetMapping("/income")
    public List<Transaction> getIncome() {
        return service.getIncomeTransactions();
    }


    @GetMapping("/chart/monthly-summary")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyChartData() {

        List<Map<String, Object>> chartData = service.getMonthlySummaryForChart();
        return ResponseEntity.ok(chartData);
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {

        System.out.println("Authenticated user: " + SecurityContextHolder.getContext().getAuthentication());
        
        Transaction savedTransaction = service.addTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @GetMapping("/chart/simple-test")
    public String simpleTest() {
    System.out.println("=== SIMPLE TEST ENDPOINT HIT ===");
    return "This is a simple test - no security, no service calls";
}
}
