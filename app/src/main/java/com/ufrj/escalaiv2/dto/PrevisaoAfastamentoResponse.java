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

    @SerializedName("intervalo_confianca_min")
    private double intervaloConfiancaMin;

    @SerializedName("intervalo_confianca_max")
    private double intervaloConfiancaMax;

    @SerializedName("fatores_principais")
    private List<String> fatoresPrincipais;

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

    public double getIntervaloConfiancaMin() {
        return intervaloConfiancaMin;
    }

    public void setIntervaloConfiancaMin(double intervaloConfiancaMin) {
        this.intervaloConfiancaMin = intervaloConfiancaMin;
    }

    public double getIntervaloConfiancaMax() {
        return intervaloConfiancaMax;
    }

    public void setIntervaloConfiancaMax(double intervaloConfiancaMax) {
        this.intervaloConfiancaMax = intervaloConfiancaMax;
    }

    public List<String> getFatoresPrincipais() {
        return fatoresPrincipais;
    }

    public void setFatoresPrincipais(List<String> fatoresPrincipais) {
        this.fatoresPrincipais = fatoresPrincipais;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}