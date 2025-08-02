package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class AguaResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("water_consumed")
    private int quantity;

    public AguaResponse(int id, int userId, String date, int quantity) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}