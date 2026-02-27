package com.example.fitapp;

import java.util.ArrayList;

public class TrainingDay {
    private String date;
    private int workoutsCompletedToday;
    private int estimatedtime;
    private boolean isCompleted;
    private ArrayList<Exercise> exercises;

    public TrainingDay() {
    }

    public TrainingDay(String date, int workoutsCompletedToday, int estimatedtime, boolean isCompleted, ArrayList<Exercise> exercises) {
        this.date = date;
        this.workoutsCompletedToday = workoutsCompletedToday;
        this.estimatedtime = estimatedtime;
        this.isCompleted = isCompleted;
        this.exercises = exercises;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWorkoutsCompletedToday() {
        return workoutsCompletedToday;
    }

    public void setWorkoutsCompletedToday(int workoutsCompletedToday) {
        this.workoutsCompletedToday = workoutsCompletedToday;
    }

    public int getEstimatedtime() {
        return estimatedtime;
    }

    public void setEstimatedtime(int estimatedtime) {
        this.estimatedtime = estimatedtime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }
}
