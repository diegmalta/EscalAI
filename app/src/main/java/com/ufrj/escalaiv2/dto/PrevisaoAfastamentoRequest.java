package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class PrevisaoAfastamentoRequest {
    @SerializedName("buscou_atendimento")
    private boolean buscouAtendimento;

    @SerializedName("reincidencia")
    private boolean reincidencia;

    @SerializedName("lesoes_previas")
    private int lesoesPrevias;

    @SerializedName("lesao_id")
    private int lesaoId;

    public PrevisaoAfastamentoRequest() {
    }

    public PrevisaoAfastamentoRequest(int lesaoId, boolean buscouAtendimento, boolean reincidencia, int lesoesPrevias) {
        this.lesaoId = lesaoId;
        this.buscouAtendimento = buscouAtendimento;
        this.reincidencia = reincidencia;
        this.lesoesPrevias = lesoesPrevias;
    }

    public boolean isBuscouAtendimento() {
        return buscouAtendimento;
    }

    public void setBuscouAtendimento(boolean buscouAtendimento) {
        this.buscouAtendimento = buscouAtendimento;
    }

    public boolean isReincidencia() {
        return reincidencia;
    }

    public void setReincidencia(boolean reincidencia) {
        this.reincidencia = reincidencia;
    }

    public int getLesaoId() {
        return lesaoId;
    }

    public void setLesaoId(int lesaoId) {
        this.lesaoId = lesaoId;
    }

    public int getLesoesPrevias() {
        return lesoesPrevias;
    }

    public void setLesoesPrevias(int lesoesPrevias) {
        this.lesoesPrevias = lesoesPrevias;
    }
}