package com.example.fitapp;

/**
 * Represents a logged sleep session.
 */
public class SleepSession {
    /** Unique identifier for the sleep session. */
    private String sessionId;
    /** Date of the sleep session. */
    private String date;
    /** Total sleep duration in hours. */
    private double sleepTime;

    /**
     * Default constructor for Firebase.
     */
    public SleepSession() {
    }

    /**
     * Constructs a new SleepSession.
     * @param sessionId Session unique ID.
     * @param date Date of sleep.
     * @param sleepTime Duration in hours.
     */
    public SleepSession(String sessionId, String date, double sleepTime) {
        this.sessionId = sessionId;
        this.date = date;
        this.sleepTime = sleepTime;
    }

    /** @return Session ID. */
    public String getSessionId() { return sessionId; }
    /** @param sessionId ID to set. */
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    /** @return Date string. */
    public String getDate() { return date; }
    /** @param date Date to set. */
    public void setDate(String date) { this.date = date; }

    /** @return Sleep hours. */
    public double getSleepTime() { return sleepTime; }
    /** @param sleepTime Hours to set. */
    public void setSleepTime(double sleepTime) { this.sleepTime = sleepTime; }
}
