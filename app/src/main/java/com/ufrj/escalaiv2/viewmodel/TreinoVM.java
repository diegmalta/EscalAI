package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.enums.TipoTreino;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.AtividadesRepository;
import com.ufrj.escalaiv2.repository.AuthRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TreinoVM extends AndroidViewModel {
    private final AtividadesRepository atividadesRepository;
    private final ExecutorService executorService;

    // LiveData para os valores selecionados
    private final MutableLiveData<Integer> selectedTreinoTipo;
    private final MutableLiveData<String> selectedTreinoTipoText;
    private final MutableLiveData<Integer> duracaoTreinoMinutos;
    private final MutableLiveData<Float> duracaoTreinoHoras; // Para o slider

    // LiveData para a tabela de resumo
    private final MutableLiveData<Map<String, Float>> resumoTreinosHoje;

    // LiveData para eventos da UI
    private final MutableLiveData<Event> uiEvent;

    public TreinoVM(Application application) {
        super(application);
        atividadesRepository = new AtividadesRepository(application);
        executorService = Executors.newSingleThreadExecutor();

        // Inicialização dos LiveData
        selectedTreinoTipo = new MutableLiveData<>(-1);
        selectedTreinoTipoText = new MutableLiveData<>("");
        duracaoTreinoMinutos = new MutableLiveData<>(60); // 1 hora por padrão
        duracaoTreinoHoras = new MutableLiveData<>(1.0f); // 1 hora por padrão
        resumoTreinosHoje = new MutableLiveData<>(new HashMap<>());
        uiEvent = new MutableLiveData<>();

        // Carregar dados do treino de hoje
        loadTodayTrainingData();
    }

    private void loadTodayTrainingData() {
        int currentUserId = getCurrentUserId();

        executorService.execute(() -> {
            // Buscar os dados de treino já registrados hoje
            Map<String, Float> resumoMap = atividadesRepository.getTodayTrainingData(currentUserId);
            resumoTreinosHoje.postValue(resumoMap);
        });
    }

    // Método para obter o ID do usuário atual
    private int getCurrentUserId() {
        return atividadesRepository.getCurrentUserId();
    }

    // Getters para LiveData
    public LiveData<Integer> getSelectedTreinoTipo() {
        return selectedTreinoTipo;
    }

    public LiveData<String> getSelectedTreinoTipoText() {
        return selectedTreinoTipoText;
    }

    public LiveData<Integer> getDuracaoTreinoMinutos() {
        return duracaoTreinoMinutos;
    }

    public LiveData<Float> getDuracaoTreinoHoras() {
        return duracaoTreinoHoras;
    }

    public LiveData<Map<String, Float>> getResumoTreinosHoje() {
        return resumoTreinosHoje;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    // Métodos para atualizar os valores selecionados
    public void updateSelectedTreinoTipo(int position) {
        selectedTreinoTipo.setValue(position);
        if (position >= 0 && position < TipoTreino.values().length) {
            TipoTreino tipoTreino = TipoTreino.values()[position];
            selectedTreinoTipoText.setValue(tipoTreino.getNome());
        } else {
            selectedTreinoTipoText.setValue("");
        }
    }

    public void updateDuracaoTreino(float horasFloat) {
        duracaoTreinoHoras.setValue(horasFloat);
        int minutos = Math.round(horasFloat * 60);
        duracaoTreinoMinutos.setValue(minutos);
    }

    // Método para salvar os dados de treino
    public void saveTreinoData() {
        Integer treinoTipo = selectedTreinoTipo.getValue();
        Integer duracaoMinutos = duracaoTreinoMinutos.getValue();

        if (treinoTipo == null || treinoTipo < 0 || duracaoMinutos == null || duracaoMinutos <= 0) {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            return;
        }

        int currentUserId = getCurrentUserId();
        AuthRepository authRepository = new AuthRepository(getApplication());
        String token = authRepository.getAuthTokenRaw();
        if (token == null) {
            token = authRepository.getAuthToken();
        }
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        // Registrar treino usando o novo repositório
        atividadesRepository.registrarTreino(currentUserId, treinoTipo, duracaoMinutos,
                token,
                new AtividadesRepository.OnActivityCallback() {
                    @Override
                    public void onSuccess() {
                        // Após salvar, recarregar o resumo de treinos
                        Map<String, Float> resumoMap = atividadesRepository.getTodayTrainingData(currentUserId);
                        resumoTreinosHoje.postValue(resumoMap);
                        uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                    }

                    @Override
                    public void onError(String message) {
                        uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                    }
                });
    }


    // Novo método para resetar o formulário
    public void resetForm() {
        selectedTreinoTipo.setValue(-1);
        selectedTreinoTipoText.setValue("");
        duracaoTreinoMinutos.setValue(60); // 1 hora por padrão
        duracaoTreinoHoras.setValue(1.0f); // 1 hora por padrão
    }
}