package com.emantahir.finance_tracker.util;

import java.util.List;

public class RiskCalculator {
    
    /**
     * Calculates standard deviation (volatility) for a list of values.
     */
    public static double calculateVolatility(List<Double> values) {
        if (values == null || values.size() < 2) return 0.0;

        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * Classifies risk based on volatility.
     */
    public static String classifyRisk(double volatility) {
        if (volatility < 0.02) return "Low";
        else if (volatility < 0.05) return "Medium";
        else return "High";
    }
}
