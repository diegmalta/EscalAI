package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class TreinoRequest {
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("tipo_treino")
    private String tipoTreino;
    
    @SerializedName("duracao_minutos")
    private int duracaoMinutos;

    public TreinoRequest(int userId, String date, String tipoTreino, int duracaoMinutos) {
        this.userId = userId;
        this.date = date;
        this.tipoTreino = tipoTreino;
        this.duracaoMinutos = duracaoMinutos;
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

    public String getTipoTreino() {
        return tipoTreino;
    }

    public void setTipoTreino(String tipoTreino) {
        this.tipoTreino = tipoTreino;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }
} 