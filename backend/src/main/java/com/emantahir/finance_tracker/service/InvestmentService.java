package com.emantahir.finance_tracker.service;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.emantahir.finance_tracker.model.Investment;
import com.emantahir.finance_tracker.model.Portfolio;
import com.emantahir.finance_tracker.model.User;
import com.emantahir.finance_tracker.repository.InvestmentRepository;
import com.emantahir.finance_tracker.repository.PortfolioRepository;
import com.emantahir.finance_tracker.repository.UserRepository;
import com.emantahir.finance_tracker.util.RiskCalculator;
import com.emantahir.finance_tracker.util.SecurityUtil;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final MarketDataService marketDataService;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtil securityUtil;

    private final AtomicReference<Instant> lastRefreshTime = new AtomicReference<>(Instant.EPOCH);
    private final ConcurrentMap<String, Instant> userLastRefresh = new ConcurrentHashMap<>();

    public InvestmentService(
        InvestmentRepository investmentRepository,
        MarketDataService marketDataService,
        UserRepository userRepository,
        PortfolioRepository portfolioRepository,
        SecurityUtil securityUtil)

        {
        this.investmentRepository = investmentRepository;
        this.marketDataService = marketDataService;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.securityUtil = securityUtil;
    }

    public void forceRefresh() {
    String username = securityUtil.getCurrentUserUsername();
    refreshAllInvestments();
    userLastRefresh.put(username, Instant.now());
}


    private Portfolio getCurrentUserPortfolio() {
        String username = securityUtil.getCurrentUserUsername();

        User currentUser = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return portfolioRepository.findByUser(currentUser)
            .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + username));
    }

    public Investment createInvestment(Investment investment) {

        String symbol = investment.getSymbol().toUpperCase();
        double shares = investment.getShares();

        double currentPrice = marketDataService.getStockPrice(symbol);
        investment.setPurchasePricePerShare(currentPrice);
        investment.setCurrentPricePerShare(currentPrice);

        return investmentRepository.save(investment);
    }


    public List<Investment> getAllInvestments() {
        List<Investment> investments = investmentRepository.findByPortfolio(getCurrentUserPortfolio());

    for (Investment inv : investments) {
        try {
            Double latestPrice = marketDataService.getStockPrice(inv.getSymbol()); // in case api fails
            if (latestPrice == null || latestPrice <= 0) {
                latestPrice = inv.getPurchasePricePerShare();
            }
            inv.setCurrentPricePerShare(latestPrice);
        } catch (Exception e) {
            System.err.println("Error fetching price for " + inv.getSymbol() + ": " + e.getMessage());
            inv.setCurrentPricePerShare(inv.getPurchasePricePerShare());
        }
    }

    return investments;
}

    public Investment refreshInvestment(Long id) {
        Investment investment = investmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Investment not found"));

        Double latestPrice = marketDataService.getStockPrice(investment.getSymbol());
        investment.setCurrentPricePerShare(latestPrice);

        return investmentRepository.save(investment);
    }

    public List<Investment> refreshAllInvestments() {
        return getAllInvestments();
}

    public Map<String, Double> getPortfolioSummary() {
        List<Investment> investments = refreshAllInvestments();

        Double totalMarketValue = 0.0;
        Double totalCostBasis = 0.0;

        for (Investment investment : investments) {
            totalCostBasis += investment.getTotalCostBasis();

            if (investment.getCurrentValue() != null) {
                totalMarketValue += investment.getCurrentValue();
            }
        }

        Double totalProfitLoss = totalMarketValue - totalCostBasis;
        
        Map<String, Double> summary = new HashMap<>();
        summary.put("totalMarketValue", totalMarketValue);
        summary.put("totalCommittedCapital", totalCostBasis);
        summary.put("totalProfitLoss", totalProfitLoss);
        
        // Calculate percentage change
        Double percentageChange = (totalCostBasis > 0)
            ? (totalProfitLoss / totalCostBasis) * 100
            : 0.0;
            
        summary.put("percentageChange", percentageChange);

        return summary;
    }

    public Map<String, Double> getAssetAllocationSummary() {
    String username = securityUtil.getCurrentUserUsername();
    Instant now = Instant.now();

    Instant lastRefresh = userLastRefresh.getOrDefault(username, Instant.EPOCH);

    boolean shouldRefresh = Duration.between(lastRefresh, now).toMinutes() > 60;

    List<Investment> investments;

    if (shouldRefresh) {
        System.out.println("Refreshing investments for " + username);
        investments = refreshAllInvestments();
        userLastRefresh.put(username, now);
    } else {
        System.out.println("Using cached investments for " + username);
        investments = getAllInvestments();
    }

    Map<String, Double> assetAllocation = new HashMap<>();

    for (Investment investment : investments) {
        String symbol = investment.getSymbol();
        Double currentValue = investment.getCurrentValue();

        if (currentValue != null && currentValue > 0) {
            assetAllocation.merge(symbol, currentValue, Double::sum);
            }
        }
        return assetAllocation;
    }

    public String calculateRiskForInvestment(Investment investment) {
        List<Double> pastReturns = marketDataService.getHistoricalReturns(investment.getSymbol());

        double volatility =  RiskCalculator.calculateVolatility(pastReturns);
        String riskLevel = RiskCalculator.classifyRisk(volatility);

        investment.setRiskLevel(riskLevel);
        investmentRepository.save(investment);

        return riskLevel;
    }
    public Investment getInvestmentById(Long id) {
        Portfolio portfolio = getCurrentUserPortfolio();

        return investmentRepository.findById(id)
            .filter(inv -> inv.getPortfolio().getId().equals(portfolio.getId()))
            .orElseThrow(() -> new RuntimeException("Investment not found or not owned by user"));
    }
}
