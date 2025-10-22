package com.emantahir.finance_tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emantahir.finance_tracker.model.Investment;
import com.emantahir.finance_tracker.service.InvestmentService;

@RestController
@CrossOrigin(origins = {"https://finance-tracker-frontend.vercel.app", "http://localhost:3000"})
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment) {
        Investment newInvestment = investmentService.createInvestment(investment);
        return ResponseEntity.ok(newInvestment);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getPortfolioSummary() {
        Map<String, Double> summary = investmentService.getPortfolioSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping
    public List<Investment> getInvestments() {
        return investmentService.getAllInvestments();
    }

    @PutMapping("/{id}/refresh")
    public Investment refreshInvestment(@PathVariable Long id) {
        return investmentService.refreshInvestment(id);
    }
    @PutMapping("/refresh-all")
    public List<Investment> refreshAll() {
        return investmentService.refreshAllInvestments();
    }

    @GetMapping("/allocation-summary/refresh")
    public ResponseEntity<Map<String, Double>> forceRefreshAllocation() {
        investmentService.forceRefresh();
        return ResponseEntity.ok(investmentService.getAssetAllocationSummary());
    }

    @GetMapping("/{id}/risk")
    public ResponseEntity<?> getInvestmentRisk(@PathVariable Long id) {
        try {
            Investment investment = investmentService.getInvestmentById(id);
            String risk = investment.getRiskLevel();
            if (risk == null || risk.isEmpty()) {
                risk = investmentService.calculateRiskForInvestment(investment);
        }
        return ResponseEntity.ok(Map.of("riskLevel", risk));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(Map.of("error", "Investment not found"));
        }
    }
}

