package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class LesaoRequest {
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("area_lesao_n1")
    private int areaLesaoN1;
    
    @SerializedName("area_lesao_n2")
    private int areaLesaoN2;
    
    @SerializedName("area_lesao_n3")
    private int areaLesaoN3;
    
    @SerializedName("massa")
    private double massa;
    
    @SerializedName("altura")
    private int altura;
    
    @SerializedName("grau_escalada")
    private int grauEscalada;
    
    @SerializedName("tempo_pratica_meses")
    private int tempoPraticaMeses;
    
    @SerializedName("frequencia_semanal")
    private int frequenciaSemanal;
    
    @SerializedName("horas_semanais")
    private int horasSemanais;
    
    @SerializedName("lesoes_previas")
    private int lesoesPrevias;
    
    @SerializedName("reincidencia")
    private boolean reincidencia;
    
    @SerializedName("buscou_atendimento")
    private boolean buscouAtendimento;
    
    @SerializedName("profissional_atendimento")
    private int profissionalAtendimento;
    
    @SerializedName("diagnostico")
    private int diagnostico;
    
    @SerializedName("profissional_tratamento")
    private int profissionalTratamento;
    
    @SerializedName("modalidade_praticada")
    private int modalidadePraticada;
    
    @SerializedName("token")
    private String token;

    // Construtor
    public LesaoRequest() {
    }

    // Getters e Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAreaLesaoN1() {
        return areaLesaoN1;
    }

    public void setAreaLesaoN1(int areaLesaoN1) {
        this.areaLesaoN1 = areaLesaoN1;
    }

    public int getAreaLesaoN2() {
        return areaLesaoN2;
    }

    public void setAreaLesaoN2(int areaLesaoN2) {
        this.areaLesaoN2 = areaLesaoN2;
    }

    public int getAreaLesaoN3() {
        return areaLesaoN3;
    }

    public void setAreaLesaoN3(int areaLesaoN3) {
        this.areaLesaoN3 = areaLesaoN3;
    }

    public double getMassa() {
        return massa;
    }

    public void setMassa(double massa) {
        this.massa = massa;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getGrauEscalada() {
        return grauEscalada;
    }

    public void setGrauEscalada(int grauEscalada) {
        this.grauEscalada = grauEscalada;
    }

    public int getTempoPraticaMeses() {
        return tempoPraticaMeses;
    }

    public void setTempoPraticaMeses(int tempoPraticaMeses) {
        this.tempoPraticaMeses = tempoPraticaMeses;
    }

    public int getFrequenciaSemanal() {
        return frequenciaSemanal;
    }

    public void setFrequenciaSemanal(int frequenciaSemanal) {
        this.frequenciaSemanal = frequenciaSemanal;
    }

    public int getHorasSemanais() {
        return horasSemanais;
    }

    public void setHorasSemanais(int horasSemanais) {
        this.horasSemanais = horasSemanais;
    }

    public int getLesoesPrevias() {
        return lesoesPrevias;
    }

    public void setLesoesPrevias(int lesoesPrevias) {
        this.lesoesPrevias = lesoesPrevias;
    }

    public boolean isReincidencia() {
        return reincidencia;
    }

    public void setReincidencia(boolean reincidencia) {
        this.reincidencia = reincidencia;
    }

    public boolean isBuscouAtendimento() {
        return buscouAtendimento;
    }

    public void setBuscouAtendimento(boolean buscouAtendimento) {
        this.buscouAtendimento = buscouAtendimento;
    }

    public int getProfissionalAtendimento() {
        return profissionalAtendimento;
    }

    public void setProfissionalAtendimento(int profissionalAtendimento) {
        this.profissionalAtendimento = profissionalAtendimento;
    }

    public int getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(int diagnostico) {
        this.diagnostico = diagnostico;
    }

    public int getProfissionalTratamento() {
        return profissionalTratamento;
    }

    public void setProfissionalTratamento(int profissionalTratamento) {
        this.profissionalTratamento = profissionalTratamento;
    }

    public int getModalidadePraticada() {
        return modalidadePraticada;
    }

    public void setModalidadePraticada(int modalidadePraticada) {
        this.modalidadePraticada = modalidadePraticada;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
