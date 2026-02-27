package com.example.fitapp;

public class SleepSession {
    private String sessionId;
    private String date;
    private double sleepTime;

    public SleepSession() {
    }

    public SleepSession(String sessionId, String date, double sleepTime) {
        this.sessionId = sessionId;
        this.date = date;
        this.sleepTime = sleepTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(double sleepTime) {
        this.sleepTime = sleepTime;
    }
}
