package com.ufrj.escalaiv2.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.Date;
import com.ufrj.escalaiv2.utils.DateTypeConverter;

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


    @ColumnInfo(name = "sexo")
    private String sexo;
    @ColumnInfo(name = "gender")
    private String gender;

    // Informacoes complementares
    @ColumnInfo(name = "weight")
    private double peso; // em Kg
    @ColumnInfo(name = "height")
    private int altura; // em cm
    @ColumnInfo(name = "grau_escalada_redpoint")
    private int grauEscaladaRedpoint;
    @ColumnInfo(name = "grau_escalada_on_sight")
    private int grauEscaladaOnsight;
    @ColumnInfo(name = "grau_escalada_boulder")
    private int grauEscaladaBoulder;
    @TypeConverters(DateTypeConverter.class)
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

    public String getNomeCompleto() {
        return nome + sobrenome;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
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

    public int getGrauEscaladaRedpoint() {
        return grauEscaladaRedpoint;
    }

    public void setGrauEscaladaRedpoint(int grauEscaladaRedpoint) {
        this.grauEscaladaRedpoint = grauEscaladaRedpoint;
    }

    public int getGrauEscaladaOnsight() {
        return grauEscaladaOnsight;
    }

    public void setGrauEscaladaOnsight(int grauEscaladaOnsight) {
        this.grauEscaladaOnsight = grauEscaladaOnsight;
    }

    public int getGrauEscaladaBoulder() {
        return grauEscaladaBoulder;
    }

    public void setGrauEscaladaBoulder(int grauEscaladaBoulder) {
        this.grauEscaladaBoulder = grauEscaladaBoulder;
    }
}
