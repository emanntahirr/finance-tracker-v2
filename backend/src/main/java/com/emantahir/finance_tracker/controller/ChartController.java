package com.emantahir.finance_tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emantahir.finance_tracker.service.ChartService;

@RestController
@RequestMapping("/api/charts")
public class ChartController {

    private final ChartService chartService;
    
    /**
     * - ensure the controller cannot exist without its required dependency.
     * - easier to mock during tests
     */
    public ChartController(ChartService chartService) {
        this.chartService= chartService;
    }

    /**
     * Bar chart data: Monthly income vs expenses
     */
    @GetMapping("/monthly-bar")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyBarChart() {
        List<Map<String, Object>> chartData = chartService.getMonthlyIncomeExpenseBarChart();
        return ResponseEntity.ok(chartData);
    }
    
    /**
     * Pie chart data: Category-wise spending
     */
    @GetMapping("/category-spending")
    public ResponseEntity<List<Map<String, Object>>> getCategorySpending() {
        List<Map<String, Object>> chartData = chartService.getCategorySpendingChart();
        return ResponseEntity.ok(chartData);
    }
    
    /**
     * Simple test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of("status", "Chart service is working"));
    }

    /**
     * savings rate to display % of income saved every month
     */
    @GetMapping("/savings-rate")
    public ResponseEntity<List<Map<String, Object>>> getSavingsRate() {
    List<Map<String, Object>> chartData = chartService.getMonthlySavingsRate();
        return ResponseEntity.ok(chartData);
    }
}
