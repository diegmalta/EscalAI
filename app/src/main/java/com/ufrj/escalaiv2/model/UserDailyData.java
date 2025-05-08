package com.ufrj.escalaiv2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "user_daily_data",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("userId")})
public class UserDailyData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "water_consumed")
    private int waterConsumed;

    @ColumnInfo(name = "joy_level")
    private int joyLevel;

    @ColumnInfo(name = "stress_level")
    private int stressLevel;

    @ColumnInfo(name = "anxiety_level")
    private int anxietyLevel;

    @ColumnInfo(name = "sadness_level")
    private int sadnessLevel;

    @ColumnInfo(name = "calm_level")
    private int calmLevel;

    // Campos para rastreamento de dor
    @ColumnInfo(name = "area_dor_n1")
    private int areaDorN1;

    @ColumnInfo(name = "area_dor_n2")
    private int areaDorN2;

    @ColumnInfo(name = "area_dor_n3")
    private int areaDorN3;

    @ColumnInfo(name = "intensidade_dor")
    private int intensidadeDor = 5;

    @ColumnInfo(name = "date")
    private String date;

    public UserDailyData(int userId, String date) {
        this.userId = userId;
        this.date = date;
        this.waterConsumed = 0;
        // Valores padrão para os níveis de humor
        this.joyLevel = 0;
        this.sadnessLevel = 0;
        this.anxietyLevel = 0;
        this.stressLevel = 0;
        this.calmLevel = 0;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWaterConsumed() {
        return waterConsumed;
    }

    public void setWaterConsumed(int waterConsumed) {
        this.waterConsumed = waterConsumed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getJoyLevel() {
        return joyLevel;
    }

    public void setJoyLevel(int joyLevel) {
        this.joyLevel = joyLevel;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public int getAnxietyLevel() {
        return anxietyLevel;
    }

    public void setAnxietyLevel(int anxietyLevel) {
        this.anxietyLevel = anxietyLevel;
    }

    public int getSadnessLevel() {
        return sadnessLevel;
    }

    public void setSadnessLevel(int sadnessLevel) {
        this.sadnessLevel = sadnessLevel;
    }

    public int getCalmLevel() {
        return calmLevel;
    }

    public void setCalmLevel(int calmLevel) {
        this.calmLevel = calmLevel;
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