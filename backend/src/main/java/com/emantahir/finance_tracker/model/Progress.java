package com.emantahir.finance_tracker.model;

public class Progress { // tracks overall user progress
    private int level;
    private int currentPoints;
    private int pointsToNextLevel;
    private int progressPercent;

    public void progress() {}

    public Progress(int level, int currentPoints, int pointsToNextLevel, int progressPercent) {

        this.level = level;
        this.currentPoints = currentPoints;
        this.pointsToNextLevel = pointsToNextLevel;
        this.progressPercent = progressPercent;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public int getPointsToNextLevel() {
        return pointsToNextLevel;
    }

    public void setPointsToNextLevel(int pointsToNextLevel) {
        this.pointsToNextLevel = pointsToNextLevel;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
}
