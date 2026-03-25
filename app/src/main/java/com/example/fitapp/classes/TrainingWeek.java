package com.example.fitapp.classes;

import java.util.ArrayList;

/**
 * Represents a full week of training.
 * Tracks the individual training days and total workouts performed.
 */
public class TrainingWeek {
    /** Unique identifier for the training week. */
    private String weekId;
    /** List of training days within this week. */
    private ArrayList<TrainingDay> trainingDays;
    /** Total number of workouts performed during this week. */
    private int workoutsThisWeek;

    /**
     * Default constructor for Firebase.
     */
    public TrainingWeek() {
    }

    /**
     * Constructs a new TrainingWeek.
     * @param weekId Week's unique ID.
     * @param trainingDays List of days.
     * @param workoutsThisWeek Count of workouts.
     */
    public TrainingWeek(String weekId, ArrayList<TrainingDay> trainingDays, int workoutsThisWeek) {
        this.weekId = weekId;
        this.trainingDays = trainingDays;
        this.workoutsThisWeek = workoutsThisWeek;
    }

    /** @return Week ID. */
    public String getWeekId() { return weekId; }
    /** @param weekId ID to set. */
    public void setWeekId(String weekId) { this.weekId = weekId; }

    /** @return List of training days. */
    public ArrayList<TrainingDay> getTrainingDays() { return trainingDays; }
    /** @param trainingDays List to set. */
    public void setTrainingDays(ArrayList<TrainingDay> trainingDays) { this.trainingDays = trainingDays; }

    /** @return Workouts count. */
    public int getWorkoutsThisWeek() { return workoutsThisWeek; }
    /** @param workoutsThisWeek Count to set. */
    public void setWorkoutsThisWeek(int workoutsThisWeek) { this.workoutsThisWeek = workoutsThisWeek; }
}
