package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.AreaCorporalN1Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN2Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN3Response;
import com.ufrj.escalaiv2.dto.ExercicioResponse;
import com.ufrj.escalaiv2.repository.ExerciciosRepository;

import java.util.List;

public class ExerciciosVM extends AndroidViewModel {
    private final ExerciciosRepository repository;
    private MutableLiveData<List<ExercicioResponse>> exerciciosLiveData;
    private MutableLiveData<List<String>> tiposLiveData;
    private MutableLiveData<List<AreaCorporalN1Response>> areasN1LiveData;
    private MutableLiveData<List<AreaCorporalN2Response>> areasN2LiveData;
    private MutableLiveData<List<AreaCorporalN3Response>> areasN3LiveData;
    
    // Filtros atuais
    private String filtroTipo = null;
    private Integer filtroN1 = null;
    private Integer filtroN2 = null;
    private Integer filtroN3 = null;
    private String filtroDificuldade = null;

    public ExerciciosVM(@NonNull Application application) {
        super(application);
        repository = new ExerciciosRepository(application);
        exerciciosLiveData = new MutableLiveData<>();
        tiposLiveData = new MutableLiveData<>();
        areasN1LiveData = new MutableLiveData<>();
        areasN2LiveData = new MutableLiveData<>();
        areasN3LiveData = new MutableLiveData<>();
    }

    public LiveData<List<ExercicioResponse>> getExercicios() {
        return exerciciosLiveData;
    }

    public LiveData<List<String>> getTipos() {
        return tiposLiveData;
    }

    public LiveData<List<AreaCorporalN1Response>> getAreasN1() {
        return areasN1LiveData;
    }

    public LiveData<List<AreaCorporalN2Response>> getAreasN2() {
        return areasN2LiveData;
    }

    public LiveData<List<AreaCorporalN3Response>> getAreasN3() {
        return areasN3LiveData;
    }

    public void carregarExercicios() {
        repository.listarExercicios(
                filtroTipo, filtroN1, filtroN2, filtroN3, filtroDificuldade, 50, 0
        ).observeForever(exercicios -> {
            exerciciosLiveData.postValue(exercicios);
        });
    }

    public void carregarTipos() {
        repository.listarTipos().observeForever(tipos -> {
            tiposLiveData.postValue(tipos);
        });
    }

    public void carregarAreasN1() {
        repository.listarAreasN1().observeForever(areas -> {
            areasN1LiveData.postValue(areas);
        });
    }

    public void carregarAreasN2(Integer n1Id) {
        repository.listarAreasN2(n1Id).observeForever(areas -> {
            areasN2LiveData.postValue(areas);
        });
    }

    public void carregarAreasN3(Integer n2Id) {
        repository.listarAreasN3(n2Id).observeForever(areas -> {
            areasN3LiveData.postValue(areas);
        });
    }

    public void darLike(int exercicioId) {
        repository.darLike(exercicioId).observeForever(success -> {
            if (success) {
                // Recarrega a lista para atualizar os likes
                carregarExercicios();
            }
        });
    }

    // Métodos para definir filtros
    public void setFiltroTipo(String tipo) {
        this.filtroTipo = tipo;
    }

    public void setFiltroN1(Integer n1Id) {
        this.filtroN1 = n1Id;
    }

    public void setFiltroN2(Integer n2Id) {
        this.filtroN2 = n2Id;
    }

    public void setFiltroN3(Integer n3Id) {
        this.filtroN3 = n3Id;
    }

    public void setFiltroDificuldade(String dificuldade) {
        this.filtroDificuldade = dificuldade;
    }

    public void limparFiltros() {
        this.filtroTipo = null;
        this.filtroN1 = null;
        this.filtroN2 = null;
        this.filtroN3 = null;
        this.filtroDificuldade = null;
    }

    public String getFiltroTipo() {
        return filtroTipo;
    }

    public Integer getFiltroN1() {
        return filtroN1;
    }

    public Integer getFiltroN2() {
        return filtroN2;
    }

    public Integer getFiltroN3() {
        return filtroN3;
    }

    public String getFiltroDificuldade() {
        return filtroDificuldade;
    }
}

