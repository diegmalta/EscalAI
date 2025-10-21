package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.enums.AreaCorporalN1;
import com.ufrj.escalaiv2.enums.AreaCorporalN2;
import com.ufrj.escalaiv2.enums.AreaCorporalN3;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.AtividadesRepository;
import com.ufrj.escalaiv2.repository.AuthRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DorVM extends AndroidViewModel {
    private final AtividadesRepository atividadesRepository;
    private final ExecutorService executorService;

    // LiveData para os valores selecionados
    private final MutableLiveData<Integer> selectedArea;
    private final MutableLiveData<Integer> selectedSubarea;
    private final MutableLiveData<Integer> selectedEspecificacao;
    private final MutableLiveData<Integer> intensidadeDor;

    // LiveData para os textos selecionados
    private final MutableLiveData<String> selectedAreaText;
    private final MutableLiveData<String> selectedSubareaText;
    private final MutableLiveData<String> selectedEspecificacaoText;

    // LiveData para habilitar/desabilitar os dropdowns
    private final MutableLiveData<Boolean> subareaEnabled;
    private final MutableLiveData<Boolean> especificacaoEnabled;

    // Dados para os dropdowns
    private final List<String> areas;
    private final Map<Integer, List<String>> subareas;
    private final Map<String, List<String>> especificacoes;

    private final MutableLiveData<Event> uiEvent;

    public DorVM(Application application) {
        super(application);
        atividadesRepository = new AtividadesRepository(application);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar os LiveData para os valores selecionados
        selectedArea = new MutableLiveData<>(-1);
        selectedSubarea = new MutableLiveData<>(-1);
        selectedEspecificacao = new MutableLiveData<>(-1);
        intensidadeDor = new MutableLiveData<>(5); // Valor padrão médio

        // Inicializar os LiveData para os textos
        selectedAreaText = new MutableLiveData<>("");
        selectedSubareaText = new MutableLiveData<>("");
        selectedEspecificacaoText = new MutableLiveData<>("");

        // Inicializar os LiveData para habilitar/desabilitar os dropdowns
        subareaEnabled = new MutableLiveData<>(false);
        especificacaoEnabled = new MutableLiveData<>(false);

        uiEvent = new MutableLiveData<>();

        // Inicializar os dados para os dropdowns
        areas = new ArrayList<>();
        for (AreaCorporalN1 area : AreaCorporalN1.values()) {
            areas.add(area.getNome());
        }

        subareas = new HashMap<>();
        // Para cada área corporal N1
        for (AreaCorporalN1 area : AreaCorporalN1.values()) {
            List<String> subareasForArea = new ArrayList<>();
            // Obter todas as subáreas relacionadas a esta área
            AreaCorporalN2[] relatedSubareas = AreaCorporalN2.getByRegiaoCorporalId(area.getId());
            for (AreaCorporalN2 subarea : relatedSubareas) {
                subareasForArea.add(subarea.getNome());
            }
            subareas.put(area.getId(), subareasForArea);
        }

        especificacoes = new HashMap<>();
        // Para cada subárea corporal N2
        for (AreaCorporalN2 subarea : AreaCorporalN2.values()) {
            List<String> especificacoesForSubarea = new ArrayList<>();
            // Obter todas as especificações relacionadas a esta subárea
            AreaCorporalN3[] relatedEspecificacoes = AreaCorporalN3.getByAreaRegiaoCorporalId(subarea.getId());
            for (AreaCorporalN3 especificacao : relatedEspecificacoes) {
                especificacoesForSubarea.add(especificacao.getNome());
            }
            especificacoes.put(subarea.getNome(), especificacoesForSubarea);
        }

        // Carregar dados de dor existentes
        loadDailyDorData();
    }

    private void loadDailyDorData() {
        executorService.execute(() -> {
            // Carregar valores de dor anteriores se existirem para hoje
            UserDailyData todayDor = atividadesRepository.getTodayDorData();

            if (todayDor != null) {
                    // Aqui você teria que ajustar de acordo com a estrutura do seu banco de dados
                    // Este é um exemplo baseado na sua estrutura atual
                    int areaId = todayDor.getAreaDorN1();
                    int subareaId = todayDor.getAreaDorN2();
                    int especificacaoId = todayDor.getAreaDorN3();
                    int intensidade = todayDor.getIntensidadeDor();

                    // Atualizar os valores no LiveData na thread principal
                    selectedArea.postValue(areaId);
                    intensidadeDor.postValue(intensidade);

                    // Configurar textos e habilitar campos
                    if (areaId >= 0 && areaId < areas.size()) {
                        selectedAreaText.postValue(areas.get(areaId));
                        subareaEnabled.postValue(true);

                        if (subareaId >= 0 && subareaId < subareas.get(areaId).size()) {
                            String subareaText = subareas.get(areaId).get(subareaId);
                            selectedSubarea.postValue(subareaId);
                            selectedSubareaText.postValue(subareaText);
                            especificacaoEnabled.postValue(true);

                            if (especificacaoId >= 0 && especificacaoId < especificacoes.get(subareaText).size()) {
                                selectedEspecificacao.postValue(especificacaoId);
                                selectedEspecificacaoText.postValue(especificacoes.get(subareaText).get(especificacaoId));
                            }
                        }
                    }
                }
        });
    }

    // Getters para os dados dos dropdowns
    public List<String> getAreas() {
        return areas;
    }

    public List<String> getSubareas(int areaId) {
        return subareas.get(areaId);
    }

    public List<String> getEspecificacoes(String subarea) {
        return especificacoes.get(subarea);
    }

    public LiveData<Integer> getSelectedArea() {
        return selectedArea;
    }

    public LiveData<Integer> getSelectedSubarea() {
        return selectedSubarea;
    }

    public LiveData<Integer> getSelectedEspecificacao() {
        return selectedEspecificacao;
    }

    public LiveData<Integer> getIntensidadeDor() {
        return intensidadeDor;
    }

    public LiveData<String> getSelectedAreaText() {
        return selectedAreaText;
    }

    public LiveData<String> getSelectedSubareaText() {
        return selectedSubareaText;
    }

    public LiveData<String> getSelectedEspecificacaoText() {
        return selectedEspecificacaoText;
    }

    public LiveData<Boolean> getSubareaEnabled() {
        return subareaEnabled;
    }

    public LiveData<Boolean> getEspecificacaoEnabled() {
        return especificacaoEnabled;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    // Métodos para atualizar os valores selecionados
    public void updateSelectedArea(int position) {
        selectedArea.setValue(position);
        selectedAreaText.setValue(areas.get(position));
        subareaEnabled.setValue(true);

        // Limpar subárea e especificação quando a área muda
        selectedSubarea.setValue(-1);
        selectedSubareaText.setValue("");
        selectedEspecificacao.setValue(-1);
        selectedEspecificacaoText.setValue("");
        especificacaoEnabled.setValue(false);
    }

    public void updateSelectedSubarea(int position) {
        selectedSubarea.setValue(position);
        selectedSubareaText.setValue(subareas.get(selectedArea.getValue()).get(position));
        especificacaoEnabled.setValue(true);

        // Limpar especificação quando a subárea muda
        selectedEspecificacao.setValue(-1);
        selectedEspecificacaoText.setValue("");
    }

    public void updateSelectedEspecificacao(int position) {
        selectedEspecificacao.setValue(position);
        selectedEspecificacaoText.setValue(
                especificacoes.get(selectedSubareaText.getValue()).get(position)
        );
    }

    public void updateIntensidadeDor(int value) {
        intensidadeDor.setValue(value);
    }

    // Método para salvar os dados
    public void saveDorData() {
        Integer area = selectedArea.getValue();
        Integer subarea = selectedSubarea.getValue();
        Integer especificacao = selectedEspecificacao.getValue();
        Integer intensidade = intensidadeDor.getValue();

        if (area == null || area < 0 || subarea == null || subarea < 0 ||
                especificacao == null || especificacao < 0 || intensidade == null) {
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
            // Registrar dor usando o novo repositório
            atividadesRepository.registrarDor(area, subarea, especificacao, intensidade, token,
                    new AtividadesRepository.OnActivityCallback() {
                        @Override
                        public void onSuccess() {
                            uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                        }

                        @Override
                        public void onError(String message) {
                            uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                        }
                    });
        });
    }


}