package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class SonoResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("sleep_time")
    private String sleepTime;

    @SerializedName("wake_time")
    private String wakeTime;

    @SerializedName("total_sleep_time_minutes")
    private int durationMinutes;

    @SerializedName("sleep_quality")
    private int quality;

    public SonoResponse() {
    }

    public SonoResponse(int userId, String date, String sleepTime, String wakeTime, int durationMinutes, int quality) {
        this.userId = userId;
        this.date = date;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.durationMinutes = durationMinutes;
        this.quality = quality;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public String getSleepTime() { return sleepTime; }
    public String getWakeTime() { return wakeTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getQuality() { return quality; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setSleepTime(String sleepTime) { this.sleepTime = sleepTime; }
    public void setWakeTime(String wakeTime) { this.wakeTime = wakeTime; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setQuality(int quality) { this.quality = quality; }
}