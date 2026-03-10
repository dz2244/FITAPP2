package com.example.fitapp;

import java.util.Map;

/**
 * Represents a structured workout program.
 * Contains information about the program's level and scheduled training days.
 */
public class WorkoutProgram {
    /** Unique identifier for the program. */
    private String programId;
    /** The name of the workout program. */
    private String name;
    /** Map representing the difficulty level of the program. */
    private Map<String, Integer> level;
    /** Map representing the scheduled training days. */
    private Map<String, Integer> days;

    /**
     * Default constructor for Firebase serialization.
     */
    public WorkoutProgram() {
    }

    /**
     * Constructs a new WorkoutProgram with specified parameters.
     * @param programId The unique ID of the program.
     * @param name The name of the program.
     * @param level The difficulty level map.
     * @param days The scheduled days map.
     */
    public WorkoutProgram(String programId, String name, Map<String, Integer> level, Map<String, Integer> days) {
        this.programId = programId;
        this.name = name;
        this.level = level;
        this.days = days;
    }

    /**
     * @return The unique program ID.
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * @param programId The program ID to set.
     */
    public void setProgramId(String programId) {
        this.programId = programId;
    }

    /**
     * @return The name of the program.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The program name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The difficulty level map.
     */
    public Map<String, Integer> getLevel() {
        return level;
    }

    /**
     * @param level The difficulty level map to set.
     */
    public void setLevel(Map<String, Integer> level) {
        this.level = level;
    }

    /**
     * @return The scheduled days map.
     */
    public Map<String, Integer> getDays() {
        return days;
    }

    /**
     * @param days The scheduled days map to set.
     */
    public void setDays(Map<String, Integer> days) {
        this.days = days;
    }
}
