package com.example.fitapp;

import java.util.Map;

public class User {
    private String userId;
    private String username;
    private int age;
    private boolean gender;
    private double height;
    private double weight;
    private Map<String, Integer> experienceLevel;
    private int workoutsPerWeek;
    private Map<String, Integer> goals;
    private int dailyTargetCalories;

    public User() {
    }

    public User(String userId, String username, int age, boolean gender, double height, double weight, Map<String, Integer> experienceLevel, int workoutsPerWeek, Map<String, Integer> goals, int dailyTargetCalories) {
        this.userId = userId;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.experienceLevel = experienceLevel;
        this.workoutsPerWeek = workoutsPerWeek;
        this.goals = goals;
        this.dailyTargetCalories = dailyTargetCalories;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Map<String, Integer> getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(Map<String, Integer> experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public int getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    public void setWorkoutsPerWeek(int workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    public Map<String, Integer> getGoals() {
        return goals;
    }

    public void setGoals(Map<String, Integer> goals) {
        this.goals = goals;
    }

    public int getDailyTargetCalories() {
        return dailyTargetCalories;
    }

    public void setDailyTargetCalories(int dailyTargetCalories) {
        this.dailyTargetCalories = dailyTargetCalories;
    }
}
