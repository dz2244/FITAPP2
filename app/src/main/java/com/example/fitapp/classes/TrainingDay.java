package com.example.fitapp.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a single day of training within a training week.
 * Contains information about workouts completed, estimated time, and specific exercises.
 */
public class TrainingDay implements Serializable {
    private String date;
    private int workoutsCompletedToday;
    private int estimatedtime;
    private boolean isCompleted;
    private ArrayList<Exercise> exercises;

    /**
     * Default constructor for Firebase serialization.
     */
    public TrainingDay() {
    }

    /**
     * Constructs a new TrainingDay with specified parameters.
     * @param date The date of the training.
     * @param workoutsCompletedToday Number of workouts finished today.
     * @param estimatedtime Estimated time in minutes.
     * @param isCompleted Completion status.
     * @param exercises List of exercises for the day.
     */
    public TrainingDay(String date, int workoutsCompletedToday, int estimatedtime, boolean isCompleted, ArrayList<Exercise> exercises) {
        this.date = date;
        this.workoutsCompletedToday = workoutsCompletedToday;
        this.estimatedtime = estimatedtime;
        this.isCompleted = isCompleted;
        this.exercises = exercises;
    }

    /** @return The date of the training day. */
    public String getDate() { return date; }
    /** @param date The date to set. */
    public void setDate(String date) { this.date = date; }

    /** @return The number of workouts completed today. */
    public int getWorkoutsCompletedToday() { return workoutsCompletedToday; }
    /** @param workoutsCompletedToday The number to set. */
    public void setWorkoutsCompletedToday(int workoutsCompletedToday) { this.workoutsCompletedToday = workoutsCompletedToday; }

    /** @return The estimated duration of the workout in minutes. */
    public int getEstimatedtime() { return estimatedtime; }
    /** @param estimatedtime The duration to set. */
    public void setEstimatedtime(int estimatedtime) { this.estimatedtime = estimatedtime; }

    /** @return true if the training for the day is completed. */
    public boolean isCompleted() { return isCompleted; }
    /** @param completed The completion status to set. */
    public void setCompleted(boolean completed) { isCompleted = completed; }

    /** @return The list of exercises for this training day. */
    public ArrayList<Exercise> getExercises() { return exercises; }
    /** @param exercises The list of exercises to set. */
    public void setExercises(ArrayList<Exercise> exercises) { this.exercises = exercises; }
}
