package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.repository.AuthRepository;
import com.ufrj.escalaiv2.repository.LesaoRepository;

import java.util.List;

public class LesaoListVM extends AndroidViewModel {
    private final LesaoRepository lesaoRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<List<LesaoResponse.LesaoData>> lesoes;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;

    public LesaoListVM(Application application) {
        super(application);
        lesaoRepository = new LesaoRepository(application);
        authRepository = new AuthRepository(application);
        lesoes = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();

        loadLesoes();
    }

    public void loadLesoes() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.getCurrentUserIdAsync(userId -> {
            android.util.Log.d("LesaoListVM", "Carregando lesões para userId: " + userId);

            lesaoRepository.getUserLesoes(userId).observeForever(response -> {
                isLoading.setValue(false);

                android.util.Log.d("LesaoListVM", "Resposta recebida: " + (response != null ? "dados=" + (response.getLesoes() != null ? response.getLesoes().size() : 0) : "null"));

                if (response != null) {
                    if (response.getLesoes() != null && !response.getLesoes().isEmpty()) {
                        android.util.Log.d("LesaoListVM", "Lesões encontradas: " + response.getLesoes().size());
                        lesoes.setValue(response.getLesoes());
                    } else if (response.getData() != null) {
                        android.util.Log.d("LesaoListVM", "Dados únicos encontrados");
                        // Fallback para o formato antigo (uma lesão)
                        lesoes.setValue(List.of(response.getData()));
                    } else {
                        android.util.Log.d("LesaoListVM", "Nenhuma lesão encontrada");
                        lesoes.setValue(List.of());
                    }
                } else {
                    String error = "Erro ao carregar lesões";
                    android.util.Log.e("LesaoListVM", "Erro: " + error);
                    errorMessage.setValue(error);
                    lesoes.setValue(List.of());
                }
            });
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
}