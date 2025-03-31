package com.ufrj.escalaiv2.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.Date;

@Entity(tableName = "users")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "name")
    private String nome;
    @ColumnInfo(name = "last_name")
    private String sobrenome;
    @ColumnInfo(name = "birthdate")
    private String dataNasc;
    @ColumnInfo(name = "phone_number")
    private String celular;
    @ColumnInfo(name = "password")
    private String senha;

    // Informacoes complementares
    @ColumnInfo(name = "weight")
    private double peso; // em Kg
    @ColumnInfo(name = "height")
    private double altura; // em cm
    @ColumnInfo(name = "weight_height_last_update")
    private Date lastUpdateDate;

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(String dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nome + sobrenome;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

}
