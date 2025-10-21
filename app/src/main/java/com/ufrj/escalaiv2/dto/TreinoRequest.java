package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class TreinoRequest {
    @SerializedName("date")
    private String date;
    
    @SerializedName("tipo_treino")
    private String tipoTreino;
    
    @SerializedName("duracao_minutos")
    private int duracaoMinutos;

    public TreinoRequest(String date, String tipoTreino, int duracaoMinutos) {
        this.date = date;
        this.tipoTreino = tipoTreino;
        this.duracaoMinutos = duracaoMinutos;
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