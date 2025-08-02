package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class HumorResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("joy_level")
    private int joyLevel;

    @SerializedName("sadness_level")
    private int sadnessLevel;

    @SerializedName("anxiety_level")
    private int anxietyLevel;

    @SerializedName("stress_level")
    private int stressLevel;

    @SerializedName("calm_level")
    private int calmLevel;

    public HumorResponse(int id, int userId, String date, int joyLevel,
                        int sadnessLevel, int anxietyLevel, int stressLevel, int calmLevel) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.joyLevel = joyLevel;
        this.sadnessLevel = sadnessLevel;
        this.anxietyLevel = anxietyLevel;
        this.stressLevel = stressLevel;
        this.calmLevel = calmLevel;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public int getJoyLevel() { return joyLevel; }
    public int getSadnessLevel() { return sadnessLevel; }
    public int getAnxietyLevel() { return anxietyLevel; }
    public int getStressLevel() { return stressLevel; }
    public int getCalmLevel() { return calmLevel; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setJoyLevel(int joyLevel) { this.joyLevel = joyLevel; }
    public void setSadnessLevel(int sadnessLevel) { this.sadnessLevel = sadnessLevel; }
    public void setAnxietyLevel(int anxietyLevel) { this.anxietyLevel = anxietyLevel; }
    public void setStressLevel(int stressLevel) { this.stressLevel = stressLevel; }
    public void setCalmLevel(int calmLevel) { this.calmLevel = calmLevel; }
}