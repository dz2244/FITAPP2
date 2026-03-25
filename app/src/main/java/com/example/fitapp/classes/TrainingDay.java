package com.example.fitapp.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a single day of training within a training week.
 * Contains information about workouts completed, estimated time, and specific exercises.
 */
public class TrainingDay implements Serializable {
    /** The date of the training day in String format. */
    private String date;
    /** The number of workouts completed on this day. */
    private int workoutsCompletedToday;
    /** The estimated duration of the workout in minutes. */
    private int estimatedtime;
    /** Indicates whether the training session for this day is completed. */
    private boolean isCompleted;
    /** List of exercises scheduled for this training day. */
    private ArrayList<Exercise> exercises;

    /**
     * Default constructor for Firebase serialization.
     */
    public TrainingDay() {
    }

    /**
     * Constructs a new TrainingDay with specified parameters.
     */
    public TrainingDay(String date, int workoutsCompletedToday, int estimatedtime, boolean isCompleted, ArrayList<Exercise> exercises) {
        this.date = date;
        this.workoutsCompletedToday = workoutsCompletedToday;
        this.estimatedtime = estimatedtime;
        this.isCompleted = isCompleted;
        this.exercises = exercises;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getWorkoutsCompletedToday() { return workoutsCompletedToday; }
    public void setWorkoutsCompletedToday(int workoutsCompletedToday) { this.workoutsCompletedToday = workoutsCompletedToday; }

    public int getEstimatedtime() { return estimatedtime; }
    public void setEstimatedtime(int estimatedtime) { this.estimatedtime = estimatedtime; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public ArrayList<Exercise> getExercises() { return exercises; }
    public void setExercises(ArrayList<Exercise> exercises) { this.exercises = exercises; }
}
