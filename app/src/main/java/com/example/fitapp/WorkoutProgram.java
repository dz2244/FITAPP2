package com.example.fitapp;

import java.util.Map;

public class WorkoutProgram {
    private String programId;
    private String name;
    private Map<String, Integer> level;
    private Map<String, Integer> days;

    public WorkoutProgram() {
    }

    public WorkoutProgram(String programId, String name, Map<String, Integer> level, Map<String, Integer> days) {
        this.programId = programId;
        this.name = name;
        this.level = level;
        this.days = days;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getLevel() {
        return level;
    }

    public void setLevel(Map<String, Integer> level) {
        this.level = level;
    }

    public Map<String, Integer> getDays() {
        return days;
    }

    public void setDays(Map<String, Integer> days) {
        this.days = days;
    }
}
