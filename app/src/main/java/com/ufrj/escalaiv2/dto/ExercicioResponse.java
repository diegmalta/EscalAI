package com.ufrj.escalaiv2.dto;

import java.util.List;

public class ExercicioResponse {
    private int id;
    private String nome;
    private String descricao;
    private String video_url;
    private String tipo;
    private Integer area_corporal_n1_id;
    private Integer area_corporal_n2_id;
    private Integer area_corporal_n3_id;
    private String dificuldade;
    private Integer duracao_minutos;
    private String repeticoes;
    private List<String> tags;
    private int likes;
    private int visualizacoes;
    private String criado_em;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getVideoUrl() {
        return video_url;
    }

    public void setVideoUrl(String video_url) {
        this.video_url = video_url;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getAreaCorporalN1Id() {
        return area_corporal_n1_id;
    }

    public void setAreaCorporalN1Id(Integer area_corporal_n1_id) {
        this.area_corporal_n1_id = area_corporal_n1_id;
    }

    public Integer getAreaCorporalN2Id() {
        return area_corporal_n2_id;
    }

    public void setAreaCorporalN2Id(Integer area_corporal_n2_id) {
        this.area_corporal_n2_id = area_corporal_n2_id;
    }

    public Integer getAreaCorporalN3Id() {
        return area_corporal_n3_id;
    }

    public void setAreaCorporalN3Id(Integer area_corporal_n3_id) {
        this.area_corporal_n3_id = area_corporal_n3_id;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public Integer getDuracaoMinutos() {
        return duracao_minutos;
    }

    public void setDuracaoMinutos(Integer duracao_minutos) {
        this.duracao_minutos = duracao_minutos;
    }

    public String getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(String repeticoes) {
        this.repeticoes = repeticoes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getVisualizacoes() {
        return visualizacoes;
    }

    public void setVisualizacoes(int visualizacoes) {
        this.visualizacoes = visualizacoes;
    }

    public String getCriadoEm() {
        return criado_em;
    }

    public void setCriadoEm(String criado_em) {
        this.criado_em = criado_em;
    }
}

