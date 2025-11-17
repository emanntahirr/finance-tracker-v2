package com.emantahir.finance_tracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class MarketDataService {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double getStockPrice(String symbol) {
        final double FALLBACK_PRICE = 100.0;

        if (symbol == null || symbol.trim().isEmpty()) {
            return FALLBACK_PRICE;
        }

        try {
            // core API Request URL Construction
            String url = apiUrl + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            // check for API-side errors
            if (response != null && response.containsKey("Error Message")) {
                System.err.println("Alpha Vantage API Error: " + response.get("Error Message"));
                return FALLBACK_PRICE;
            }

            // JSON parsing logic
            if (response != null && response.containsKey("Global Quote")) {
                Map<String, Object> quote = (Map<String, Object>) response.get("Global Quote");
                
                if (quote != null && quote.containsKey("05. price")) {
                    String priceStr = (String) quote.get("05. price");
                    // Zero or null means symbol wasn't valid or API throttled; fallback keeps app stable
                    if (priceStr == null || Double.parseDouble(priceStr) == 0.0) {
                        System.err.println("Stock symbol '" + symbol + "' not found or returned 0.0 price.");
                        return FALLBACK_PRICE;
                    }
                    
                    return Double.parseDouble(priceStr);
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Client Error: " + e.getStatusCode());
        } catch (Exception e) {
            System.err.println("Unexpected Error fetching stock price: " + e.getMessage());
        }
        
        return FALLBACK_PRICE;
    }

@SuppressWarnings("unchecked")
public List<Double> getHistoricalReturns(String symbol) {
    final List<Double> fallbackReturns = List.of(0.01, -0.02, 0.015, -0.005, 0.02);
    // fallback: ensures risk calculation still works even if API fails
    if (symbol == null || symbol.trim().isEmpty()) {
        return fallbackReturns;
    }

    try {
        String url = apiUrl + "?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + apiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        // If API returns no data, keep using our fallback to avoid crashing the feature
        if (response == null || !response.containsKey("Time Series (Daily)")) {
            System.err.println("Alpha Vantage API returned no data for symbol: " + symbol);
            return fallbackReturns;
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
        List<Double> prices = new ArrayList<>();
        // Extract close prices; limit to 30 days to avoid unnecessary overhead
        for (Object value : timeSeries.values()) {
            Map<String, Object> dailyData = (Map<String, Object>) value;
            String closeStr = (String) dailyData.get("4. close");

            if (closeStr != null) {
                prices.add(Double.parseDouble(closeStr));
            }

            if (prices.size() >= 30) break; // Limit to last 30 days for performance
        }

        // Calculate daily returns
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double prev = prices.get(i - 1);
            double curr = prices.get(i);
            if (prev > 0) {
                returns.add((curr - prev) / prev);
            }
        }

        return returns.isEmpty() ? fallbackReturns : returns;

    } catch (Exception e) {
        System.err.println("Error fetching historical data for " + symbol + ": " + e.getMessage());
        return fallbackReturns;
    }
}
}
