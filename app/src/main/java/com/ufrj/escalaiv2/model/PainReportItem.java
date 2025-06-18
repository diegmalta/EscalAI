package com.ufrj.escalaiv2.model;
public class PainReportItem {
    private String localDor;
    private String tempoDesdeInicio;
    private int intensidadeMaxima;
    private float intensidadeMedia;
    private String tempoSemDor;

    public PainReportItem(String localDor, String tempoDesdeInicio, int intensidadeMaxima, float intensidadeMedia, String tempoSemDor) {
        this.localDor = localDor;
        this.tempoDesdeInicio = tempoDesdeInicio;
        this.intensidadeMaxima = intensidadeMaxima;
        this.intensidadeMedia = intensidadeMedia;
        this.tempoSemDor = tempoSemDor;
    }

    public String getLocalDor() {
        return localDor;
    }

    public String getTempoDesdeInicio() {
        return tempoDesdeInicio;
    }

    public int getIntensidadeMaxima() {
        return intensidadeMaxima;
    }

    public float getIntensidadeMedia() {
        return intensidadeMedia;
    }

    public String getTempoSemDor() {
        return tempoSemDor;
    }
}
