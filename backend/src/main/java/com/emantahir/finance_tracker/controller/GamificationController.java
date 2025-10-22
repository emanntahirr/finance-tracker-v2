package com.emantahir.finance_tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emantahir.finance_tracker.model.Achievement;
import com.emantahir.finance_tracker.model.Progress;
import com.emantahir.finance_tracker.model.Transaction;
import com.emantahir.finance_tracker.repository.TransactionRepository;
import com.emantahir.finance_tracker.service.GamificationService;

@RestController
@RequestMapping("/gamification")
public class GamificationController {

    private final TransactionRepository repository;
    private final GamificationService service;

    public GamificationController(TransactionRepository repository, GamificationService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/points")
    public Map<String, Integer> getPoints() {
        List<Transaction> txs = repository.findAll();
        int points = service.calculatePoints(txs);
        return Map.of("points" , points);
    }

    @GetMapping("/achievements")
    public List<Achievement> getAchievements() {
        List<Transaction> txs = repository.findAll();
        return service.evaluateAchievements(txs);
    }

    @GetMapping("/progress")
    public Progress getProgress() {
        List<Transaction> txs = repository.findAll();
        int points = service.calculatePoints(txs);
        return service.progressFromPoints(points);
    }
}
