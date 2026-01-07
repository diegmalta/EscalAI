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
    private final MutableLiveData<Integer> duracaoTreinoHoras;
    private final MutableLiveData<Integer> duracaoTreinoMinutosParte; // minutos no intervalo {0,15,30,45}
    private final MutableLiveData<String> duracaoTreinoDisplay;

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
        duracaoTreinoHoras = new MutableLiveData<>(1);    // componente horas
        duracaoTreinoMinutosParte = new MutableLiveData<>(0); // componente minutos (0,15,30,45)
        duracaoTreinoDisplay = new MutableLiveData<>("1h 00min");
        resumoTreinosHoje = new MutableLiveData<>(new HashMap<>());
        uiEvent = new MutableLiveData<>();

        // Carregar dados do treino de hoje
        loadTodayTrainingData();
    }

    private void loadTodayTrainingData() {
        executorService.execute(() -> {
            // Buscar os dados de treino já registrados hoje
            Map<String, Float> resumoMap = atividadesRepository.getTodayTrainingData();
            resumoTreinosHoje.postValue(resumoMap);
        });
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

    public LiveData<Integer> getDuracaoTreinoHoras() { return duracaoTreinoHoras; }
    public LiveData<Integer> getDuracaoTreinoMinutosParte() { return duracaoTreinoMinutosParte; }
    public LiveData<String> getDuracaoTreinoDisplay() { return duracaoTreinoDisplay; }

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

    public void updateDuracaoTreino(int horas, int minutosParte) {
        // minutosParte deve ser um dos {0,15,30,45}
        if (minutosParte != 0 && minutosParte != 15 && minutosParte != 30 && minutosParte != 45) {
            minutosParte = 0;
        }
        // Impedir 24h com minutos > 0
        if (horas >= 24) {
            horas = 24;
            minutosParte = 0;
        } else if (horas < 0) {
            horas = 0;
        }

        duracaoTreinoHoras.setValue(horas);
        duracaoTreinoMinutosParte.setValue(minutosParte);

        int totalMinutos = (horas * 60) + minutosParte;
        duracaoTreinoMinutos.setValue(totalMinutos);
        duracaoTreinoDisplay.setValue(formatarDuracao(horas, minutosParte));
    }

    private String formatarDuracao(int horas, int minutosParte) {
        String h = String.format("%02dh", horas);
        String m = String.format("%02dmin", minutosParte);
        return horas == 0 ? m : (horas == 24 ? "24h 00min" : h + " " + m);
    }

    // Método para salvar os dados de treino
    public void saveTreinoData() {
        Integer treinoTipo = selectedTreinoTipo.getValue();
        Integer duracaoMinutos = duracaoTreinoMinutos.getValue();

        android.util.Log.d("TreinoVM", "saveTreinoData - tipo: " + treinoTipo + 
                ", duracaoMinutos: " + duracaoMinutos);

        if (treinoTipo == null || treinoTipo < 0 || duracaoMinutos == null || duracaoMinutos <= 0) {
            android.util.Log.e("TreinoVM", "Validação falhou!");
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            return;
        }

        atividadesRepository.getCurrentUserId(currentUserId -> {
            AuthRepository authRepository = new AuthRepository(getApplication());
            String token = authRepository.getAuthTokenRaw();
            if (token == null) {
                token = authRepository.getAuthToken();
            }
            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }

            // Registrar treino usando o novo repositório
            android.util.Log.d("TreinoVM", "Chamando registrarTreino no repositório");
            atividadesRepository.registrarTreino(treinoTipo, duracaoMinutos,
                    token,
                    new AtividadesRepository.OnActivityCallback() {
                        @Override
                        public void onSuccess() {
                            android.util.Log.d("TreinoVM", "onSuccess - Treino salvo com sucesso!");
                            // Atualizar resumo em thread de fundo, com fallback ao treino atual
                            android.util.Log.d("TreinoVM", "Buscando resumo de treinos atualizado...");
                            atividadesRepository.getTodayTrainingDataAsync(treinoTipo, duracaoMinutos, resumoMap -> {
                                android.util.Log.d("TreinoVM", "Resumo recebido: " + resumoMap);
                                resumoTreinosHoje.postValue(resumoMap);
                                uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                                android.util.Log.d("TreinoVM", "UI atualizada com sucesso");
                            });
                        }

                        @Override
                        public void onError(String message) {
                            android.util.Log.e("TreinoVM", "onError - Erro ao salvar treino: " + message);
                            uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                        }
                    });
        });
    }


    // Novo método para resetar o formulário
    public void resetForm() {
        selectedTreinoTipo.setValue(-1);
        selectedTreinoTipoText.setValue("");
        duracaoTreinoMinutos.setValue(60); // 1 hora por padrão
        duracaoTreinoHoras.setValue(1);    // componente horas
        duracaoTreinoMinutosParte.setValue(0);
        duracaoTreinoDisplay.setValue("1h 00min");
    }
}