package com.ufrj.escalaiv2.dto;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("celular")
    private String celular;

    @SerializedName("sexo")
    private String sexo;

    @SerializedName("gender")
    private String gender;

    @SerializedName("peso")
    private Double peso;

    @SerializedName("altura")
    private Integer altura;

    @SerializedName("grau_escalada_redpoint")
    private Integer grauEscaladaRedpoint;

    @SerializedName("grau_escalada_onsight")
    private Integer grauEscaladaOnsight;

    @SerializedName("grau_escalada_boulder")
    private Integer grauEscaladaBoulder;

    @SerializedName("weight_height_last_update")
    private String weightHeightLastUpdate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Construtor
    public UserProfileResponse() {
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Integer getAltura() {
        return altura;
    }

    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    public Integer getGrauEscaladaRedpoint() {
        return grauEscaladaRedpoint;
    }

    public void setGrauEscaladaRedpoint(Integer grauEscaladaRedpoint) {
        this.grauEscaladaRedpoint = grauEscaladaRedpoint;
    }

    public Integer getGrauEscaladaOnsight() {
        return grauEscaladaOnsight;
    }

    public void setGrauEscaladaOnsight(Integer grauEscaladaOnsight) {
        this.grauEscaladaOnsight = grauEscaladaOnsight;
    }

    public Integer getGrauEscaladaBoulder() {
        return grauEscaladaBoulder;
    }

    public void setGrauEscaladaBoulder(Integer grauEscaladaBoulder) {
        this.grauEscaladaBoulder = grauEscaladaBoulder;
    }

    public String getWeightHeightLastUpdate() {
        return weightHeightLastUpdate;
    }

    public void setWeightHeightLastUpdate(String weightHeightLastUpdate) {
        this.weightHeightLastUpdate = weightHeightLastUpdate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
