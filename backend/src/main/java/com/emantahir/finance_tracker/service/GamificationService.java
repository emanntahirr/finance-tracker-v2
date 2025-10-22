package com.emantahir.finance_tracker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.emantahir.finance_tracker.model.Achievement;
import com.emantahir.finance_tracker.model.Progress;
import com.emantahir.finance_tracker.model.Transaction;

@Service
public class GamificationService {

    public int calculatePoints(List<Transaction> txs) {
        int points = 0;
        for (Transaction t : txs) {
            double amt = t.getAmount();
            if (amt > 0) {
                points += 10;
            } else if (amt < 0) {
                points += 5;
            }
        }
        return points;
    }

    public double balance(List<Transaction> txs) {
        double total = 0;
        for (Transaction t : txs) {
            total += t.getAmount();
        }

        return total;
    }

    public List<Achievement> evaluateAchievements(List<Transaction> txs) {
        List<Achievement> list = new ArrayList<>();
        LocalDate today = LocalDate.now();

        boolean first = txs.size() >= 1;
        list.add(new Achievement(
            "first transaction",
            "you logged your first transaction",
            first,
            first ? today : null
        ));

        double bal = balance(txs);
        boolean saver = bal >= 100.0;
        list.add(new Achievement(
            "saver",
            "maintain a positive balance of 100 or more",
            saver,
            saver ? today : null
        ));

        int expenseCount = 0;
        for (Transaction t : txs) {
            if (t.getAmount() < 0) expenseCount++;
        }

        boolean budgetMaster = expenseCount >= 10;
        list.add(new Achievement(
            "Budget Master",
            "Log 10 or more expenses.",
            budgetMaster,
            budgetMaster ? today : null
        ));

        boolean streak3 = distinctTransactionDays(txs) >= 3;
        list.add(new Achievement(
            "Getting Consistent",
            "Log transactions on 3 different days.",
            streak3,
            streak3 ? today : null
        ));

        return list;
    }

    private int distinctTransactionDays(List<Transaction> txs) {
        Set<LocalDate> days = new HashSet<>();
        for (Transaction t : txs) {
            LocalDate d = t.getDate();
            if (d != null) days.add(d);
        }

        return days.size();
    }

    public Progress progressFromPoints(int points) {
        int level = points / 100;
        int pointsIntoLevel = points % 100;
        int pointsToNext = 100 - pointsIntoLevel;
        int percent = (int) Math.round((pointsIntoLevel / 100) * 100);
        return new Progress(
            level,
            pointsIntoLevel,
            pointsToNext,
            percent
        );
    }
}
