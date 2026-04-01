package com.example.fitapp.classes;

import java.util.Map;

/**
 * Represents a user profile in the application.
 * Stores personal biometrics, preferences, goals, and targets.
 */
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
    private long registrationDate;
    private String currentProgramId;

    /**
     * Default constructor for Firebase.
     */
    public User() {
    }

    /**
     * Constructs a new User profile.
     */
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
        this.registrationDate = System.currentTimeMillis();
    }

    /** @return Unique user ID. */
    public String getUserId() { return userId; }
    /** @param userId ID to set. */
    public void setUserId(String userId) { this.userId = userId; }

    /** @return Username. */
    public String getUsername() { return username; }
    /** @param username Name to set. */
    public void setUsername(String username) { this.username = username; }

    /** @return User's age. */
    public int getAge() { return age; }
    /** @param age Age to set. */
    public void setAge(int age) { this.age = age; }

    /** @return true if male. */
    public boolean isGender() { return gender; }
    /** @param gender Gender boolean to set. */
    public void setGender(boolean gender) { this.gender = gender; }

    /** @return Height in meters. */
    public double getHeight() { return height; }
    /** @param height Height to set. */
    public void setHeight(double height) { this.height = height; }

    /** @return Weight in kilograms. */
    public double getWeight() { return weight; }
    /** @param weight Weight to set. */
    public void setWeight(double weight) { this.weight = weight; }

    /** @return Experience level map. */
    public Map<String, Integer> getExperienceLevel() { return experienceLevel; }
    /** @param experienceLevel Map to set. */
    public void setExperienceLevel(Map<String, Integer> experienceLevel) { this.experienceLevel = experienceLevel; }

    /** @return Weekly workout target. */
    public int getWorkoutsPerWeek() { return workoutsPerWeek; }
    /** @param workoutsPerWeek Target to set. */
    public void setWorkoutsPerWeek(int workoutsPerWeek) { this.workoutsPerWeek = workoutsPerWeek; }

    /** @return Fitness goals map. */
    public Map<String, Integer> getGoals() { return goals; }
    /** @param goals Goals map to set. */
    public void setGoals(Map<String, Integer> goals) { this.goals = goals; }

    /** @return Target calories. */
    public int getDailyTargetCalories() { return dailyTargetCalories; }
    /** @param dailyTargetCalories Calories to set. */
    public void setDailyTargetCalories(int dailyTargetCalories) { this.dailyTargetCalories = dailyTargetCalories; }

    /** @return Registration date in milliseconds. */
    public long getRegistrationDate() { return registrationDate; }
    /** @param registrationDate Date to set. */
    public void setRegistrationDate(long registrationDate) { this.registrationDate = registrationDate; }

    /** @return Current program ID. */
    public String getCurrentProgramId() { return currentProgramId; }
    /** @param currentProgramId ID to set. */
    public void setCurrentProgramId(String currentProgramId) { this.currentProgramId = currentProgramId; }
}
