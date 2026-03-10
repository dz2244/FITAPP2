package com.example.fitapp;

/**
 * Represents a single physical exercise within a workout session.
 */
public class Exercise {
    /** The name of the exercise (e.g., "Bench Press"). */
    private String exerciseName;
    /** The number of sets to be performed. */
    private int sets;
    /** The number of repetitions per set. */
    private int reps;
    /** The recommended rest time between sets, in seconds. */
    private int restTime;

    /**
     * Default constructor for Firebase serialization.
     */
    public Exercise() {
    }

    /**
     * Constructs a new Exercise with specified parameters.
     * @param exerciseName Name of the exercise.
     * @param sets Number of sets.
     * @param reps Number of repetitions.
     * @param restTime Rest time in seconds.
     */
    public Exercise(String exerciseName, int sets, int reps, int restTime) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }

    /**
     * @return The name of the exercise.
     */
    public String getExerciseName() {
        return exerciseName;
    }

    /**
     * @param exerciseName The exercise name to set.
     */
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
     * @return The number of sets.
     */
    public int getSets() {
        return sets;
    }

    /**
     * @param sets The number of sets to set.
     */
    public void setSets(int sets) {
        this.sets = sets;
    }

    /**
     * @return The number of repetitions.
     */
    public int getReps() {
        return reps;
    }

    /**
     * @param reps The number of repetitions to set.
     */
    public void setReps(int reps) {
        this.reps = reps;
    }

    /**
     * @return The rest time in seconds.
     */
    public int getRestTime() {
        return restTime;
    }

    /**
     * @param restTime The rest time to set.
     */
    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }
}
