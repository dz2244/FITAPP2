package com.example.fitapp.classes;

import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a structured workout program.
 * Contains information about the program's level and scheduled training weeks.
 */
public class WorkoutProgram {
    private String programId;
    private String name;
    private Map<String, Integer> level;
    private ArrayList<TrainingWeek> weeks;

    public WorkoutProgram() {
    }

    /**
     * Constructs a new WorkoutProgram with specified parameters.
     * @param programId The unique ID of the program.
     * @param name The name of the program.
     * @param level The difficulty level map.
     * @param weeks The list of training weeks.
     */
    public WorkoutProgram(String programId, String name, Map<String, Integer> level, ArrayList<TrainingWeek> weeks) {
        this.programId = programId;
        this.name = name;
        this.level = level;
        this.weeks = weeks;
    }


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
     * @return The list of training weeks.
     */
    public ArrayList<TrainingWeek> getWeeks() {
        return weeks;
    }

    /**
     * @param weeks The list of training weeks to set.
     */
    public void setWeeks(ArrayList<TrainingWeek> weeks) {
        this.weeks = weeks;
    }
}
