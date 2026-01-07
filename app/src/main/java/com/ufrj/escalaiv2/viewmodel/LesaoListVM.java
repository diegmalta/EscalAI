package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.repository.AuthRepository;
import com.ufrj.escalaiv2.repository.LesaoRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LesaoListVM extends AndroidViewModel {
    private final LesaoRepository lesaoRepository;
    private final AuthRepository authRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<List<LesaoResponse.LesaoData>> lesoes;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;

    public LesaoListVM(Application application) {
        super(application);
        lesaoRepository = new LesaoRepository(application);
        authRepository = new AuthRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        lesoes = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();

        loadLesoes();
    }

    public void loadLesoes() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        android.util.Log.d("LesaoListVM", "Carregando lesões");

        lesaoRepository.getUserLesoes().observeForever(response -> {
            isLoading.postValue(false);

            android.util.Log.d("LesaoListVM", "Resposta recebida: " + (response != null ? "dados=" + (response.getLesoes() != null ? response.getLesoes().size() : 0) : "null"));

                if (response != null) {
                    if (response.getLesoes() != null && !response.getLesoes().isEmpty()) {
                        android.util.Log.d("LesaoListVM", "Lesões encontradas: " + response.getLesoes().size());
                        lesoes.postValue(response.getLesoes());
                    } else if (response.getData() != null) {
                        android.util.Log.d("LesaoListVM", "Dados únicos encontrados");
                        // Fallback para o formato antigo (uma lesão)
                        lesoes.postValue(List.of(response.getData()));
                    } else {
                        android.util.Log.d("LesaoListVM", "Nenhuma lesão encontrada");
                        lesoes.postValue(List.of());
                    }
                } else {
                    String error = "Erro ao carregar lesões";
                    android.util.Log.e("LesaoListVM", "Erro: " + error);
                    errorMessage.postValue(error);
                    lesoes.postValue(List.of());
                }
        });
    }

    public LiveData<List<LesaoResponse.LesaoData>> getLesoes() {
        return lesoes;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private int getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    public void concluirLesao(int lesaoId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        lesaoRepository.concluirLesao(lesaoId).observeForever(response -> {
            isLoading.postValue(false);

            if (response != null && response.isSuccess()) {
                // Recarregar lesões após concluir
                loadLesoes();
            } else {
                String error = response != null && response.getMessage() != null 
                    ? response.getMessage() 
                    : "Erro ao concluir lesão";
                errorMessage.postValue(error);
            }
        });
    }

    public void reabrirLesao(LesaoResponse.LesaoData lesao) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Criar request com os dados da lesão, mas com data_conclusao = null
        LesaoRequest request = new LesaoRequest();
        request.setAreaLesaoN1(lesao.getAreaLesaoN1());
        request.setAreaLesaoN2(lesao.getAreaLesaoN2());
        request.setAreaLesaoN3(lesao.getAreaLesaoN3());
        request.setMassa(lesao.getMassa());
        request.setAltura(lesao.getAltura());
        request.setGrauEscalada(lesao.getGrauEscalada());
        request.setTempoPraticaMeses(lesao.getTempoPraticaMeses());
        request.setFrequenciaSemanal(lesao.getFrequenciaSemanal());
        request.setHorasSemanais(lesao.getHorasSemanais());
        request.setLesoesPrevias(lesao.getLesoesPrevias());
        request.setReincidencia(lesao.isReincidencia());
        request.setBuscouAtendimento(lesao.isBuscouAtendimento());
        request.setProfissionalAtendimento(lesao.getProfissionalAtendimento());
        request.setDiagnostico(lesao.getDiagnostico());
        request.setProfissionalTratamento(lesao.getProfissionalTratamento());
        request.setModalidadePraticada(lesao.getModalidadePraticada());
        request.setDataInicio(lesao.getDataInicio());
        request.setDataConclusao(null); // Remove a data de conclusão para reabrir

        lesaoRepository.reabrirLesao(lesao.getId(), request).observeForever(response -> {
            isLoading.postValue(false);

            if (response != null && response.isSuccess()) {
                // Recarregar lesões após reabrir
                loadLesoes();
            } else {
                String error = response != null && response.getMessage() != null 
                    ? response.getMessage() 
                    : "Erro ao reabrir lesão";
                errorMessage.postValue(error);
            }
        });
    }

    public void deleteLesao(int lesaoId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        lesaoRepository.deleteLesao(lesaoId).observeForever(response -> {
            isLoading.postValue(false);

            if (response != null && response.isSuccess()) {
                // Recarregar lesões após excluir
                loadLesoes();
            } else {
                String error = response != null && response.getMessage() != null 
                    ? response.getMessage() 
                    : "Erro ao excluir lesão";
                errorMessage.postValue(error);
            }
        });
    }
}