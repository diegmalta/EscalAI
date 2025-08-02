package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class DorResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("area_dor_n1")
    private int areaDorN1;

    @SerializedName("area_dor_n2")
    private int areaDorN2;

    @SerializedName("area_dor_n3")
    private int areaDorN3;

    @SerializedName("intensidade_dor")
    private int intensidadeDor;

    public DorResponse(int id, int userId, String date, int areaDorN1,
                      int areaDorN2, int areaDorN3, int intensidadeDor) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.areaDorN1 = areaDorN1;
        this.areaDorN2 = areaDorN2;
        this.areaDorN3 = areaDorN3;
        this.intensidadeDor = intensidadeDor;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public int getAreaDorN1() { return areaDorN1; }
    public int getAreaDorN2() { return areaDorN2; }
    public int getAreaDorN3() { return areaDorN3; }
    public int getIntensidadeDor() { return intensidadeDor; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setAreaDorN1(int areaDorN1) { this.areaDorN1 = areaDorN1; }
    public void setAreaDorN2(int areaDorN2) { this.areaDorN2 = areaDorN2; }
    public void setAreaDorN3(int areaDorN3) { this.areaDorN3 = areaDorN3; }
    public void setIntensidadeDor(int intensidadeDor) { this.intensidadeDor = intensidadeDor; }
}