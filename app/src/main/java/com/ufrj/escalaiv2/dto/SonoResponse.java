package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class SonoResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("duration_minutes")
    private int durationMinutes;

    @SerializedName("quality")
    private int quality;

    public SonoResponse(int id, int userId, String date, int durationMinutes, int quality) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.quality = quality;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getQuality() { return quality; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setQuality(int quality) { this.quality = quality; }
}