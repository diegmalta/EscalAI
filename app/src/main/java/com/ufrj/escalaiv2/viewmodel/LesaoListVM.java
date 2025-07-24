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
        int currentUserId = getCurrentUserId();
        isLoading.setValue(true);
        errorMessage.setValue(null);

        lesaoRepository.getUserLesoes(currentUserId).observeForever(response -> {
            isLoading.setValue(false);

            if (response != null && response.isSuccess()) {
                if (response.getLesoes() != null && !response.getLesoes().isEmpty()) {
                    lesoes.setValue(response.getLesoes());
                } else if (response.getData() != null) {
                    // Fallback para o formato antigo (uma lesão)
                    lesoes.setValue(List.of(response.getData()));
                } else {
                    lesoes.setValue(List.of());
                }
            } else {
                String error = response != null ? response.getMessage() : "Erro ao carregar lesões";
                errorMessage.setValue(error);
                lesoes.setValue(List.of());
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
}