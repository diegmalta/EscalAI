package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DadosUsuarioResponse {
    @SerializedName("agua")
    private List<AguaResponse> agua;

    @SerializedName("dores")
    private List<DorResponse> dores;

    @SerializedName("humor")
    private List<HumorResponse> humor;

    @SerializedName("sono")
    private List<SonoResponse> sono;

    @SerializedName("treinos")
    private List<TreinoResponse> treinos;

    @SerializedName("periodo_dias")
    private int periodoDias;

    @SerializedName("data_inicio")
    private String dataInicio;

    @SerializedName("data_fim")
    private String dataFim;

    public DadosUsuarioResponse(List<AguaResponse> agua, List<DorResponse> dores,
                              List<HumorResponse> humor, List<SonoResponse> sono,
                              List<TreinoResponse> treinos, int periodoDias,
                              String dataInicio, String dataFim) {
        this.agua = agua;
        this.dores = dores;
        this.humor = humor;
        this.sono = sono;
        this.treinos = treinos;
        this.periodoDias = periodoDias;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Getters
    public List<AguaResponse> getAgua() { return agua; }
    public List<DorResponse> getDores() { return dores; }
    public List<HumorResponse> getHumor() { return humor; }
    public List<SonoResponse> getSono() { return sono; }
    public List<TreinoResponse> getTreinos() { return treinos; }
    public int getPeriodoDias() { return periodoDias; }
    public String getDataInicio() { return dataInicio; }
    public String getDataFim() { return dataFim; }

    // Setters
    public void setAgua(List<AguaResponse> agua) { this.agua = agua; }
    public void setDores(List<DorResponse> dores) { this.dores = dores; }
    public void setHumor(List<HumorResponse> humor) { this.humor = humor; }
    public void setSono(List<SonoResponse> sono) { this.sono = sono; }
    public void setTreinos(List<TreinoResponse> treinos) { this.treinos = treinos; }
    public void setPeriodoDias(int periodoDias) { this.periodoDias = periodoDias; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }
}