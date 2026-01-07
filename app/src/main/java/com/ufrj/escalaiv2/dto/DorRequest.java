package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class DorRequest {
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

    public DorRequest(String date, int areaDorN1, int areaDorN2, 
                     int areaDorN3, int intensidadeDor) {
        this.date = date;
        this.areaDorN1 = areaDorN1;
        this.areaDorN2 = areaDorN2;
        this.areaDorN3 = areaDorN3;
        this.intensidadeDor = intensidadeDor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAreaDorN1() {
        return areaDorN1;
    }

    public void setAreaDorN1(int areaDorN1) {
        this.areaDorN1 = areaDorN1;
    }

    public int getAreaDorN2() {
        return areaDorN2;
    }

    public void setAreaDorN2(int areaDorN2) {
        this.areaDorN2 = areaDorN2;
    }

    public int getAreaDorN3() {
        return areaDorN3;
    }

    public void setAreaDorN3(int areaDorN3) {
        this.areaDorN3 = areaDorN3;
    }

    public int getIntensidadeDor() {
        return intensidadeDor;
    }

    public void setIntensidadeDor(int intensidadeDor) {
        this.intensidadeDor = intensidadeDor;
    }
} 