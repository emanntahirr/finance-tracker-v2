package com.emantahir.finance_tracker.model;

import java.time.LocalDate;

public class Achievement { // tracks individual achievements
    private String name;
    private String description;
    private boolean unlocked;
    private LocalDate dateUnlocked;

    public Achievement() {}
    public Achievement(String name, String description, boolean unlocked, LocalDate dateUnlocked) {
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
        this.dateUnlocked = dateUnlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LocalDate getDateUnlokced() {
        return dateUnlocked;
    }

    public void setDateUnlocked(LocalDate dateUnlocked) {
        this.dateUnlocked = dateUnlocked;
    }
}
