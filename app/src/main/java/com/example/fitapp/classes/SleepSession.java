package com.example.fitapp.classes;

/**
 * Represents a logged sleep session.
 */
public class SleepSession {
    private String sessionId;
    private String date;
    private double sleepTime;
    private boolean wokeUpInMiddle;

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
     * @param wokeUpInMiddle Whether the user woke up.
     */
    public SleepSession(String sessionId, String date, double sleepTime, boolean wokeUpInMiddle) {
        this.sessionId = sessionId;
        this.date = date;
        this.sleepTime = sleepTime;
        this.wokeUpInMiddle = wokeUpInMiddle;
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

    /** @return Whether the user woke up. */
    public boolean isWokeUpInMiddle() { return wokeUpInMiddle; }
    /** @param wokeUpInMiddle The status to set. */
    public void setWokeUpInMiddle(boolean wokeUpInMiddle) { this.wokeUpInMiddle = wokeUpInMiddle; }
}
