package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class TreinoResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("tipo_treino")
    private String tipoTreino;

    @SerializedName("duracao_minutos")
    private int durationMinutes;

    public TreinoResponse(int id, int userId, String date, String tipoTreino, int durationMinutes) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.tipoTreino = tipoTreino;
        this.durationMinutes = durationMinutes;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public String getTipoTreino() { return tipoTreino; }
    public int getDurationMinutes() { return durationMinutes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setTipoTreino(String tipoTreino) { this.tipoTreino = tipoTreino; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}