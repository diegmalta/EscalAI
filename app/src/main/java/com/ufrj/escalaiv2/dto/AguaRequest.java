package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class AguaRequest {
    @SerializedName("date")
    private String date;

    @SerializedName("water_consumed")
    private int waterConsumed;

    public AguaRequest(String date, int waterConsumed) {
        this.date = date;
        this.waterConsumed = waterConsumed;
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