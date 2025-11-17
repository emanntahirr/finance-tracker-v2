package com.emantahir.finance_tracker.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Represents an investment entry belonging to a user.
 * Seprated investments from transactions to allow special handling
 */
@Entity
@Table(name = "investments")
@JsonIgnoreProperties("portfolio")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();
    

    @Column(nullable = false)
    private Double shares;
    

    @Column(nullable = false)
    private Double purchasePricePerShare;
    
    @Column(name = "risk_level")
    private String riskLevel;

    private String type;

    // --- Transient Fields ---
    @Transient
    private Double currentPricePerShare;
    
    @Transient
    private Double currentValue; // shares * currentPricePerShare
    
    @Transient
    private Double profitLoss;   // currentValue - totalCostBasis

    /**
     * modelled as many-to-one so that we can support multiple investments per user
     * while letting user-lever queries run
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    public Investment() {}
    
    public Double getTotalCostBasis() {
        return this.shares * this.purchasePricePerShare;
    }

    // --- Standard Getters and Setters for Persisted Fields ---
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getShares() {
        return shares;
    }
    public void setShares(Double shares) {
        this.shares = shares;
    }
    
    public Double getPurchasePricePerShare() {
        return purchasePricePerShare;
    }
    public void setPurchasePricePerShare(Double purchasePricePerShare) {
        this.purchasePricePerShare = purchasePricePerShare;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
        // --- Getters and Setters for Transient Fields ---
        /**
         * Sets live market price and updates dependent fields
         * Since currentValue & profitloss depend on market price, recompute to keep logic
         * consistent instead of having to repeat calculations.
         */
    public void setCurrentPricePerShare(Double currentPricePerShare) {
        this.currentPricePerShare = currentPricePerShare;
        if (currentPricePerShare != null && this.shares != null && this.purchasePricePerShare != null) {
            this.currentValue = this.shares * currentPricePerShare;
            this.profitLoss = this.currentValue - this.getTotalCostBasis();
        }
    }
    
    public Double getCurrentPricePerShare() {
        return currentPricePerShare;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }
}