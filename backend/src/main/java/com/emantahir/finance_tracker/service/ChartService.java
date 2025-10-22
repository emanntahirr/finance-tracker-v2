package com.emantahir.finance_tracker.service;

import org.springframework.stereotype.Service;
import com.emantahir.finance_tracker.model.Portfolio;
import com.emantahir.finance_tracker.model.Transaction;
import com.emantahir.finance_tracker.repository.TransactionRepository;
import java.util.*;

@Service
public class ChartService {
    
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    
    public ChartService(TransactionService transactionService, 
                        TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }
    
    public List<Map<String, Object>> getMonthlyIncomeExpenseBarChart() {
        try {
            Portfolio portfolio = getCurrentUserPortfolio();
            
            // SQL aggregation
            List<Object[]> rawData = transactionRepository.findMonthlyIncomeExpenseSummary(portfolio);
            
            return convertToBarChartFormat(rawData);
            
        } catch (Exception e) {
            // Fallback to Java processing if SQL fails
            System.out.println("SQL aggregation failed, using Java fallback: " + e.getMessage());
            return getMonthlyIncomeExpenseBarChartJavaFallback();
        }
    }
    
    /**
     * Category-wise spending with percentages
     */
    public List<Map<String, Object>> getCategorySpendingChart() {
        Portfolio portfolio = getCurrentUserPortfolio();
        
        // Get all expenses for this portfolio
        List<Transaction> expenses = transactionRepository.findByPortfolioAndAmountLessThan(portfolio, 0.0);
        
        double totalExpenses = expenses.stream()
            .mapToDouble(t -> Math.abs(t.getAmount()))
            .sum();

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction expense : expenses) {
            String category = expense.getCategory() != null ? expense.getCategory() : "Other";
            double amount = Math.abs(expense.getAmount());
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }
        
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double percentage = totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0;
            
            Map<String, Object> categoryData = new LinkedHashMap<>();
            categoryData.put("category", entry.getKey());
            categoryData.put("amount", entry.getValue());
            categoryData.put("percentage", Math.round(percentage * 100.0) / 100.0);
            chartData.add(categoryData);
        }
        
        chartData.sort((a, b) -> Double.compare((Double) b.get("amount"), (Double) a.get("amount")));
        
        return chartData;
    }
    
    /**
     * Monthly savings rate trend
     */
    public List<Map<String, Object>> getMonthlySavingsRate() {
        Portfolio portfolio = getCurrentUserPortfolio();
        
        // Use the existing method
        List<Object[]> monthlyData = transactionRepository.findMonthlyIncomeExpenseSummary(portfolio);
        
        List<Map<String, Object>> savingsData = new ArrayList<>();
        for (Object[] row : monthlyData) {
            String month = (String) row[0];
            Double income = (Double) row[1];
            Double expense = (Double) row[2];
            
            // Only include months with income
            if (income != null && income > 0) {
                double savings = income - expense;
                double savingsRate = (savings / income) * 100;
                
                Map<String, Object> monthData = new LinkedHashMap<>();
                monthData.put("month", month);
                monthData.put("savingsRate", Math.round(savingsRate * 100.0) / 100.0);
                monthData.put("savings", savings);
                monthData.put("income", income);
                monthData.put("expense", expense);
                
                savingsData.add(monthData);
            }
        }
        
        return savingsData;
    }
    
    // Helper methods
    private List<Map<String, Object>> convertToBarChartFormat(List<Object[]> rawData) {
        List<Map<String, Object>> chartData = new ArrayList<>();
        
        for (Object[] row : rawData) {
            String month = (String) row[0];
            Double income = (Double) row[1];
            Double expense = (Double) row[2];
            
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("month", month);
            monthData.put("income", income != null ? income : 0.0);
            monthData.put("expense", expense != null ? expense : 0.0);
            monthData.put("savings", (income != null ? income : 0.0) - (expense != null ? expense : 0.0));
            
            chartData.add(monthData);
        }
        
        return chartData;
    }
    
    private List<Map<String, Object>> getMonthlyIncomeExpenseBarChartJavaFallback() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        Map<String, Map<String, Double>> monthlyData = new TreeMap<>();
        
        for (Transaction transaction : transactions) {
            String monthKey = getMonthKey(transaction.getDate());
            monthlyData.putIfAbsent(monthKey, new HashMap<>());
            
            if (transaction.getAmount() > 0) {
                double current = monthlyData.get(monthKey).getOrDefault("income", 0.0);
                monthlyData.get(monthKey).put("income", current + transaction.getAmount());
            } else {
                double current = monthlyData.get(monthKey).getOrDefault("expense", 0.0);
                monthlyData.get(monthKey).put("expense", current + Math.abs(transaction.getAmount()));
            }
        }
        
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> entry : monthlyData.entrySet()) {
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("month", entry.getKey());
            monthData.put("income", entry.getValue().getOrDefault("income", 0.0));
            monthData.put("expense", entry.getValue().getOrDefault("expense", 0.0));
            chartData.add(monthData);
        }
        
        return chartData;
    }

    private Portfolio getCurrentUserPortfolio() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            if (!transactions.isEmpty()) {
                return transactions.get(0).getPortfolio();
            } else {
                throw new RuntimeException("No transactions found for current user");
            }
        } catch (Exception e) {
            System.out.println("Error getting portfolio: " + e.getMessage());
            throw new RuntimeException("Could not get user portfolio: " + e.getMessage());
        }
    }
    
    private String getMonthKey(java.time.LocalDate date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
}