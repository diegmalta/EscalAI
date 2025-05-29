package com.ufrj.escalaiv2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ufrj.escalaiv2.utils.MapConverter;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "user_daily_data",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("userId")})
@TypeConverters(MapConverter.class)
public class UserDailyData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "date")
    private String date;

    // Agua
    @ColumnInfo(name = "water_consumed")
    private int waterConsumed;

    // Humor
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

    // Dor
    @ColumnInfo(name = "area_dor_n1")
    private int areaDorN1;
    @ColumnInfo(name = "area_dor_n2")
    private int areaDorN2;
    @ColumnInfo(name = "area_dor_n3")
    private int areaDorN3;
    @ColumnInfo(name = "intensidade_dor")
    private int intensidadeDor = 5;

    // Sono
    @ColumnInfo(name = "sleep_time")
    private String sleepTime;
    @ColumnInfo(name = "wake_time")
    private String wakeTime;
    @ColumnInfo(name = "total_sleep_time")
    private int totalSleepTimeInMinutes;
    @ColumnInfo(name = "sleep_quality")
    private int sleepQuality = 3;

    // Treinos
    @ColumnInfo(name = "treinos_data") // Coluna para armazenar o JSON do Map de treinos
    private Map<String, Integer> treinosMap; // Mapa: Chave = TipoTreino.name() ou ID, Valor = Duração total em minutos

    // Construtor
    public UserDailyData(int userId, String date) {
        this.userId = userId;
        this.date = date;
        this.waterConsumed = 0;
        this.joyLevel = 0;
        this.sadnessLevel = 0;
        this.anxietyLevel = 0;
        this.stressLevel = 0;
        this.calmLevel = 0;
        this.sleepTime = "00:00";
        this.wakeTime = "08:00";
        this.totalSleepTimeInMinutes = 480;
        this.sleepQuality = 3;
        this.intensidadeDor = 5;
        this.treinosMap = new HashMap<>();
    }

    // --- Getters e Setters ---

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

    public int getTotalSleepTimeInMinutes() {
        return totalSleepTimeInMinutes;
    }

    public void setTotalSleepTimeInMinutes(int totalSleepTimeInMinutes) {
        this.totalSleepTimeInMinutes = totalSleepTimeInMinutes;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public Map<String, Integer> getTreinosMap() {
        return treinosMap == null ? new HashMap<>() : treinosMap;
    }

    public void setTreinosMap(Map<String, Integer> treinosMap) {
        this.treinosMap = treinosMap;
    }
}

