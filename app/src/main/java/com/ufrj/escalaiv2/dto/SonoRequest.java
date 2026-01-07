package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class SonoRequest {
    @SerializedName("date")
    private String date;
    
    @SerializedName("sleep_time")
    private String sleepTime;
    
    @SerializedName("wake_time")
    private String wakeTime;
    
    @SerializedName("total_sleep_time_minutes")
    private int totalSleepTimeMinutes;
    
    @SerializedName("sleep_quality")
    private int sleepQuality;

    public SonoRequest(String date, String sleepTime, String wakeTime, 
                      int totalSleepTimeMinutes, int sleepQuality) {
        this.date = date;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalSleepTimeMinutes = totalSleepTimeMinutes;
        this.sleepQuality = sleepQuality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(String wakeTime) {
        this.wakeTime = wakeTime;
    }

    public int getTotalSleepTimeMinutes() {
        return totalSleepTimeMinutes;
    }

    public void setTotalSleepTimeMinutes(int totalSleepTimeMinutes) {
        this.totalSleepTimeMinutes = totalSleepTimeMinutes;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }
} 