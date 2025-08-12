package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PrevisaoAfastamentoResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("tempo_afastamento_dias")
    private int tempoAfastamentoDias;

    @SerializedName("tempo_afastamento_semanas")
    private int tempoAfastamentoSemanas;

    @SerializedName("confianca")
    private double confianca;

    @SerializedName("fatores_principais")
    private List<String> fatoresPrincipais;

    @SerializedName("recomendacoes")
    private List<String> recomendacoes;

    @SerializedName("message")
    private String message;

    public PrevisaoAfastamentoResponse() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTempoAfastamentoDias() {
        return tempoAfastamentoDias;
    }

    public void setTempoAfastamentoDias(int tempoAfastamentoDias) {
        this.tempoAfastamentoDias = tempoAfastamentoDias;
    }

    public int getTempoAfastamentoSemanas() {
        return tempoAfastamentoSemanas;
    }

    public void setTempoAfastamentoSemanas(int tempoAfastamentoSemanas) {
        this.tempoAfastamentoSemanas = tempoAfastamentoSemanas;
    }

    public double getConfianca() {
        return confianca;
    }

    public void setConfianca(double confianca) {
        this.confianca = confianca;
    }

    public List<String> getFatoresPrincipais() {
        return fatoresPrincipais;
    }

    public void setFatoresPrincipais(List<String> fatoresPrincipais) {
        this.fatoresPrincipais = fatoresPrincipais;
    }

    public List<String> getRecomendacoes() {
        return recomendacoes;
    }

    public void setRecomendacoes(List<String> recomendacoes) {
        this.recomendacoes = recomendacoes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}