package com.example.fitapp;

import java.util.ArrayList;

public class TrainingWeek {
    private String weekId;
    private ArrayList<TrainingDay> trainingDays;
    private int workoutsThisWeek;

    public TrainingWeek() {
    }

    public TrainingWeek(String weekId, ArrayList<TrainingDay> trainingDays, int workoutsThisWeek) {
        this.weekId = weekId;
        this.trainingDays = trainingDays;
        this.workoutsThisWeek = workoutsThisWeek;
    }

    public String getWeekId() {
        return weekId;
    }

    public void setWeekId(String weekId) {
        this.weekId = weekId;
    }

    public ArrayList<TrainingDay> getTrainingDays() {
        return trainingDays;
    }

    public void setTrainingDays(ArrayList<TrainingDay> trainingDays) {
        this.trainingDays = trainingDays;
    }

    public int getWorkoutsThisWeek() {
        return workoutsThisWeek;
    }

    public void setWorkoutsThisWeek(int workoutsThisWeek) {
        this.workoutsThisWeek = workoutsThisWeek;
    }
}
