package com.example.fitapp;

public class Exercise {
    private String exerciseName;
    private int sets;
    private int reps;
    private int restTime;

    public Exercise() {
    }

    public Exercise(String exerciseName, int sets, int reps, int restTime) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }
}
