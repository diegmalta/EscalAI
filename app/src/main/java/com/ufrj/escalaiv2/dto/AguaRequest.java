package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class AguaRequest {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("water_consumed")
    private int waterConsumed;

    public AguaRequest(int userId, String date, int waterConsumed) {
        this.userId = userId;
        this.date = date;
        this.waterConsumed = waterConsumed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWaterConsumed() {
        return waterConsumed;
    }

    public void setWaterConsumed(int waterConsumed) {
        this.waterConsumed = waterConsumed;
    }
}