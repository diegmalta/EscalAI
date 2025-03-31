package com.ufrj.escalaiv2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "users_daily_data",
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

    @ColumnInfo(name = "date")
    private String date;

    public UserDailyData() {
        // Set the date to today when creating a new instance
        this.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Getters and setters...
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
}