package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.UserDailyDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DorVM extends AndroidViewModel {
    private final UserDailyDataRepository userDailyDataRepository;
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
        userDailyDataRepository = new UserDailyDataRepository(application);
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
        areas.add("Área 1");
        areas.add("Área 2");
        areas.add("Área 3");

        subareas = new HashMap<>();

        // Subáreas para Área 1
        List<String> subareasArea1 = new ArrayList<>();
        subareasArea1.add("Subárea 1A");
        subareasArea1.add("Subárea 1B");
        subareasArea1.add("Subárea 1C");
        subareas.put(0, subareasArea1);

        // Subáreas para Área 2
        List<String> subareasArea2 = new ArrayList<>();
        subareasArea2.add("Subárea 2A");
        subareasArea2.add("Subárea 2B");
        subareasArea2.add("Subárea 2C");
        subareas.put(1, subareasArea2);

        // Subáreas para Área 3
        List<String> subareasArea3 = new ArrayList<>();
        subareasArea3.add("Subárea 3A");
        subareasArea3.add("Subárea 3B");
        subareasArea3.add("Subárea 3C");
        subareas.put(2, subareasArea3);

        especificacoes = new HashMap<>();

        // Especificações para cada subárea
        // Para Área 1
        especificacoes.put("Subárea 1A", createEspecificacoes("1A"));
        especificacoes.put("Subárea 1B", createEspecificacoes("1B"));
        especificacoes.put("Subárea 1C", createEspecificacoes("1C"));

        // Para Área 2
        especificacoes.put("Subárea 2A", createEspecificacoes("2A"));
        especificacoes.put("Subárea 2B", createEspecificacoes("2B"));
        especificacoes.put("Subárea 2C", createEspecificacoes("2C"));

        // Para Área 3
        especificacoes.put("Subárea 3A", createEspecificacoes("3A"));
        especificacoes.put("Subárea 3B", createEspecificacoes("3B"));
        especificacoes.put("Subárea 3C", createEspecificacoes("3C"));

        // Carregar dados do banco se existirem
        loadDailyDorData();
    }

    private List<String> createEspecificacoes(String prefix) {
        List<String> esp = new ArrayList<>();
        esp.add("Esp " + prefix + "1");
        esp.add("Esp " + prefix + "2");
        esp.add("Esp " + prefix + "3");
        return esp;
    }

    private void loadDailyDorData() {
        int currentUserId = getCurrentUserId();

        executorService.execute(() -> {
            // Carregar valores de dor anteriores se existirem para hoje
            UserDailyData todayDor = userDailyDataRepository.getTodayDorData(currentUserId);

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

    // Getters para os LiveData
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

        int currentUserId = getCurrentUserId();

        // Salvar os dados no banco de dados
        boolean success = userDailyDataRepository.saveDorData(
                currentUserId,
                area,
                subarea,
                especificacao,
                intensidade
        );

        if (success) {
            uiEvent.setValue(Event.SHOW_SUCCESS_MESSAGE);
        } else {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
        }
    }

    // Método para obter o ID do usuário atual
    private int getCurrentUserId() {
        // Implementação real para obter o ID do usuário logado
        return 1; // Substituir pela implementação real
    }
}