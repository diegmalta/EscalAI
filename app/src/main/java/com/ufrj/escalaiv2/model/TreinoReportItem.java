package com.ufrj.escalaiv2.model;

public class TreinoReportItem {
    private String tipoTreino;
    private String data;
    private int duracaoMinutos;

    public TreinoReportItem(String tipoTreino, String data, int duracaoMinutos) {
        this.tipoTreino = tipoTreino;
        this.data = data;
        this.duracaoMinutos = duracaoMinutos;
    }

    // Getters
    public String getTipoTreino() { return tipoTreino; }
    public String getData() { return data; }
    public int getDuracaoMinutos() { return duracaoMinutos; }

    // Setters
    public void setTipoTreino(String tipoTreino) { this.tipoTreino = tipoTreino; }
    public void setData(String data) { this.data = data; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}