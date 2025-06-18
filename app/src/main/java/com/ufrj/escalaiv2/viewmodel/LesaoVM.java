package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.enums.AreaCorporalN1;
import com.ufrj.escalaiv2.enums.AreaCorporalN2;
import com.ufrj.escalaiv2.enums.AreaCorporalN3;
import com.ufrj.escalaiv2.enums.DiagnosticoLesao;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.enums.GrauEscaladaBrasileiro;
import com.ufrj.escalaiv2.enums.ProfissionalSaude;
import com.ufrj.escalaiv2.enums.TipoTreino;
import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.repository.AuthRepository;
import com.ufrj.escalaiv2.repository.LesaoRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LesaoVM extends AndroidViewModel {
    private final LesaoRepository lesaoRepository;
    private final AuthRepository authRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExecutorService executorService;

    // LiveData para os valores selecionados de área da lesão
    private final MutableLiveData<Integer> selectedArea;
    private final MutableLiveData<Integer> selectedSubarea;
    private final MutableLiveData<Integer> selectedEspecificacao;

    // LiveData para os textos selecionados de área da lesão
    private final MutableLiveData<String> selectedAreaText;
    private final MutableLiveData<String> selectedSubareaText;
    private final MutableLiveData<String> selectedEspecificacaoText;

    // LiveData para habilitar/desabilitar os dropdowns de área da lesão
    private final MutableLiveData<Boolean> subareaEnabled;
    private final MutableLiveData<Boolean> especificacaoEnabled;

    // LiveData para os dados do usuário
    private final MutableLiveData<Double> massa;
    private final MutableLiveData<Integer> altura;
    private final MutableLiveData<Integer> grauEscalada;
    private final MutableLiveData<String> grauEscaladaText;

    // LiveData para os dados de prática de escalada
    private final MutableLiveData<Integer> tempoPraticaMeses;
    private final MutableLiveData<Integer> frequenciaSemanal;
    private final MutableLiveData<Integer> horasSemanais;
    private final MutableLiveData<Integer> lesoesPrevias;

    // LiveData para os dados de lesão
    private final MutableLiveData<Boolean> reincidencia;
    private final MutableLiveData<Boolean> buscouAtendimento;
    private final MutableLiveData<Integer> profissionalAtendimento;
    private final MutableLiveData<String> profissionalAtendimentoText;
    private final MutableLiveData<Integer> diagnostico;
    private final MutableLiveData<String> diagnosticoText;
    private final MutableLiveData<Integer> profissionalTratamento;
    private final MutableLiveData<String> profissionalTratamentoText;
    private final MutableLiveData<Integer> modalidadePraticada;
    private final MutableLiveData<String> modalidadePraticadaText;

    // LiveData para controle de UI
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<Event> uiEvent;
    private final MutableLiveData<Boolean> atendimentoFieldsEnabled;

    // Dados para os dropdowns de área da lesão
    private final List<String> areas;
    private final Map<Integer, List<String>> subareas;
    private final Map<String, List<String>> especificacoes;

    public LesaoVM(Application application) {
        super(application);
        lesaoRepository = new LesaoRepository(application);
        authRepository = new AuthRepository(application);
        usuarioRepository = new UsuarioRepository(application);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar os LiveData para os valores selecionados de área da lesão
        selectedArea = new MutableLiveData<>(-1);
        selectedSubarea = new MutableLiveData<>(-1);
        selectedEspecificacao = new MutableLiveData<>(-1);

        // Inicializar os LiveData para os textos de área da lesão
        selectedAreaText = new MutableLiveData<>("");
        selectedSubareaText = new MutableLiveData<>("");
        selectedEspecificacaoText = new MutableLiveData<>("");

        // Inicializar os LiveData para habilitar/desabilitar os dropdowns de área da lesão
        subareaEnabled = new MutableLiveData<>(false);
        especificacaoEnabled = new MutableLiveData<>(false);

        // Inicializar os LiveData para os dados do usuário
        massa = new MutableLiveData<>(0.0d);
        altura = new MutableLiveData<>(0);
        grauEscalada = new MutableLiveData<>(0);
        grauEscaladaText = new MutableLiveData<>("");

        // Inicializar os LiveData para os dados de prática de escalada
        tempoPraticaMeses = new MutableLiveData<>(0);
        frequenciaSemanal = new MutableLiveData<>(0);
        horasSemanais = new MutableLiveData<>(0);
        lesoesPrevias = new MutableLiveData<>(0);

        // Inicializar os LiveData para os dados de lesão
        reincidencia = new MutableLiveData<>(false);
        buscouAtendimento = new MutableLiveData<>(false);
        profissionalAtendimento = new MutableLiveData<>(0);
        profissionalAtendimentoText = new MutableLiveData<>("");
        diagnostico = new MutableLiveData<>(0);
        diagnosticoText = new MutableLiveData<>("");
        profissionalTratamento = new MutableLiveData<>(0);
        profissionalTratamentoText = new MutableLiveData<>("");
        modalidadePraticada = new MutableLiveData<>(0);
        modalidadePraticadaText = new MutableLiveData<>("");

        // Inicializar os LiveData para controle de UI
        isLoading = new MutableLiveData<>(false);
        uiEvent = new MutableLiveData<>();
        atendimentoFieldsEnabled = new MutableLiveData<>(false);

        // Inicializar os dados para os dropdowns de área da lesão
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

        // Carregar dados do usuário
        loadUserData();
    }

    private void loadUserData() {
        int currentUserId = getCurrentUserId();
        isLoading.setValue(true);

        // Buscar dados do usuário no banco local usando LiveData
        LiveData<Usuario> usuarioLiveData = usuarioRepository.getUsuario(currentUserId);

        // Observar o LiveData para obter os dados do usuário quando disponíveis
        usuarioLiveData.observeForever(new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                // Remover o observer após obter os dados para evitar vazamentos de memória
                usuarioLiveData.removeObserver(this);

                if (usuario != null) {
                    massa.setValue(usuario.getPeso());
                    altura.setValue(usuario.getAltura());

                    // Grau de escalada
                    int grauId = usuario.getGrauEscaladaRedpoint();
                    grauEscalada.setValue(grauId);
                    GrauEscaladaBrasileiro grau = GrauEscaladaBrasileiro.getById(grauId);
                    grauEscaladaText.setValue(grau.getNome());
                }

                // Buscar dados de lesão na API
                lesaoRepository.getUserLesoes(currentUserId).observeForever(response -> {
                    isLoading.setValue(false);

                    if (response != null && response.isSuccess() && response.getData() != null) {
                        LesaoResponse.LesaoData data = response.getData();

                        // Área da lesão
                        int areaId = data.getAreaLesaoN1();
                        selectedArea.setValue(areaId);
                        if (areaId >= 0 && areaId < areas.size()) {
                            selectedAreaText.setValue(areas.get(areaId));
                            subareaEnabled.setValue(true);

                            int subareaId = data.getAreaLesaoN2();
                            if (subareaId >= 0 && subareaId < subareas.get(areaId).size()) {
                                String subareaText = subareas.get(areaId).get(subareaId);
                                selectedSubarea.setValue(subareaId);
                                selectedSubareaText.setValue(subareaText);
                                especificacaoEnabled.setValue(true);

                                int especificacaoId = data.getAreaLesaoN3();
                                if (especificacaoId >= 0 && especificacaoId < especificacoes.get(subareaText).size()) {
                                    selectedEspecificacao.setValue(especificacaoId);
                                    selectedEspecificacaoText.setValue(especificacoes.get(subareaText).get(especificacaoId));
                                }
                            }
                        }

                        // Dados de prática
                        tempoPraticaMeses.setValue(data.getTempoPraticaMeses());
                        frequenciaSemanal.setValue(data.getFrequenciaSemanal());
                        horasSemanais.setValue(data.getHorasSemanais());
                        lesoesPrevias.setValue(data.getLesoesPrevias());

                        // Dados de lesão
                        reincidencia.setValue(data.isReincidencia());
                        buscouAtendimento.setValue(data.isBuscouAtendimento());

                        if (data.isBuscouAtendimento()) {
                            atendimentoFieldsEnabled.setValue(true);

                            // Profissional de atendimento
                            int profAtendId = data.getProfissionalAtendimento();
                            profissionalAtendimento.setValue(profAtendId);
                            ProfissionalSaude profAtend = ProfissionalSaude.getById(profAtendId);
                            profissionalAtendimentoText.setValue(profAtend.getNome());

                            // Diagnóstico
                            int diagId = data.getDiagnostico();
                            diagnostico.setValue(diagId);
                            DiagnosticoLesao diag = DiagnosticoLesao.getById(diagId);
                            diagnosticoText.setValue(diag.getNome());

                            // Profissional de tratamento
                            int profTratId = data.getProfissionalTratamento();
                            profissionalTratamento.setValue(profTratId);
                            ProfissionalSaude profTrat = ProfissionalSaude.getById(profTratId);
                            profissionalTratamentoText.setValue(profTrat.getNome());

                            // Modalidade praticada
                            int modalId = data.getModalidadePraticada();
                            modalidadePraticada.setValue(modalId);
                            TipoTreino modal = TipoTreino.getById(modalId);
                            modalidadePraticadaText.setValue(modal.getNome());
                        }
                    }
                });
            }
        });
    }

    // Getters para os dados dos dropdowns de área da lesão
    public List<String> getAreas() {
        return areas;
    }

    public List<String> getSubareas(int areaId) {
        return subareas.get(areaId);
    }

    public List<String> getEspecificacoes(String subarea) {
        return especificacoes.get(subarea);
    }

    // Getters para os dados de graus de escalada
    public String[] getGrausEscalada() {
        return GrauEscaladaBrasileiro.getAllNames();
    }

    // Getters para os dados de profissionais de saúde
    public String[] getProfissionaisSaude() {
        return ProfissionalSaude.getAllNames();
    }

    // Getters para os dados de diagnósticos
    public String[] getDiagnosticos() {
        return DiagnosticoLesao.getAllNames();
    }

    // Getters para os dados de modalidades
    public String[] getModalidades() {
        return TipoTreino.getAllNames();
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

    public LiveData<Double> getMassa() {
        return massa;
    }

    public LiveData<Integer> getAltura() {
        return altura;
    }

    public LiveData<Integer> getGrauEscalada() {
        return grauEscalada;
    }

    public LiveData<String> getGrauEscaladaText() {
        return grauEscaladaText;
    }

    public LiveData<Integer> getTempoPraticaMeses() {
        return tempoPraticaMeses;
    }

    public LiveData<Integer> getFrequenciaSemanal() {
        return frequenciaSemanal;
    }

    public LiveData<Integer> getHorasSemanais() {
        return horasSemanais;
    }

    public LiveData<Integer> getLesoesPrevias() {
        return lesoesPrevias;
    }

    public LiveData<Boolean> getReincidencia() {
        return reincidencia;
    }

    public LiveData<Boolean> getBuscouAtendimento() {
        return buscouAtendimento;
    }

    public LiveData<Integer> getProfissionalAtendimento() {
        return profissionalAtendimento;
    }

    public LiveData<String> getProfissionalAtendimentoText() {
        return profissionalAtendimentoText;
    }

    public LiveData<Integer> getDiagnostico() {
        return diagnostico;
    }

    public LiveData<String> getDiagnosticoText() {
        return diagnosticoText;
    }

    public LiveData<Integer> getProfissionalTratamento() {
        return profissionalTratamento;
    }

    public LiveData<String> getProfissionalTratamentoText() {
        return profissionalTratamentoText;
    }

    public LiveData<Integer> getModalidadePraticada() {
        return modalidadePraticada;
    }

    public LiveData<String> getModalidadePraticadaText() {
        return modalidadePraticadaText;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    public LiveData<Boolean> getAtendimentoFieldsEnabled() {
        return atendimentoFieldsEnabled;
    }

    // Métodos para atualizar os valores
    public void updateSelectedArea(int position) {
        selectedArea.setValue(position);
        selectedAreaText.setValue(areas.get(position));
        subareaEnabled.setValue(true);
        selectedSubarea.setValue(-1);
        selectedSubareaText.setValue("");
        especificacaoEnabled.setValue(false);
        selectedEspecificacao.setValue(-1);
        selectedEspecificacaoText.setValue("");
    }

    public void updateSelectedSubarea(int position) {
        selectedSubarea.setValue(position);
        int areaId = selectedArea.getValue();
        if (areaId >= 0 && position >= 0 && position < subareas.get(areaId).size()) {
            String subareaText = subareas.get(areaId).get(position);
            selectedSubareaText.setValue(subareaText);
            especificacaoEnabled.setValue(true);
            selectedEspecificacao.setValue(-1);
            selectedEspecificacaoText.setValue("");
        }
    }

    public void updateSelectedEspecificacao(int position) {
        selectedEspecificacao.setValue(position);
        String subareaText = selectedSubareaText.getValue();
        if (subareaText != null && !subareaText.isEmpty() && position >= 0 && position < especificacoes.get(subareaText).size()) {
            selectedEspecificacaoText.setValue(especificacoes.get(subareaText).get(position));
        }
    }

    public void updateMassa(double value) {
        massa.setValue(value);
    }

    public void updateAltura(int value) {
        altura.setValue(value);
    }

    public void updateGrauEscalada(int position) {
        grauEscalada.setValue(position);
        grauEscaladaText.setValue(GrauEscaladaBrasileiro.getAllNames()[position]);
    }

    public void updateTempoPraticaMeses(int value) {
        tempoPraticaMeses.setValue(value);
    }

    public void updateFrequenciaSemanal(int value) {
        frequenciaSemanal.setValue(value);
    }

    public void updateHorasSemanais(int value) {
        horasSemanais.setValue(value);
    }

    public void updateLesoesPrevias(int value) {
        lesoesPrevias.setValue(value);
    }

    public void updateReincidencia(boolean value) {
        reincidencia.setValue(value);
    }

    public void updateBuscouAtendimento(boolean value) {
        buscouAtendimento.setValue(value);
        atendimentoFieldsEnabled.setValue(value);
    }

    public void updateProfissionalAtendimento(int position) {
        profissionalAtendimento.setValue(position);
        profissionalAtendimentoText.setValue(ProfissionalSaude.getAllNames()[position]);
    }

    public void updateDiagnostico(int position) {
        diagnostico.setValue(position);
        diagnosticoText.setValue(DiagnosticoLesao.getAllNames()[position]);
    }

    public void updateProfissionalTratamento(int position) {
        profissionalTratamento.setValue(position);
        profissionalTratamentoText.setValue(ProfissionalSaude.getAllNames()[position]);
    }

    public void updateModalidadePraticada(int position) {
        modalidadePraticada.setValue(position);
        modalidadePraticadaText.setValue(TipoTreino.getAllNames()[position]);
    }

    // Método para salvar os dados
    public void saveLesaoData() {
        isLoading.setValue(true);

        LesaoRequest request = new LesaoRequest();
        request.setUserId(getCurrentUserId());

        // Área da lesão
        request.setAreaLesaoN1(selectedArea.getValue());
        request.setAreaLesaoN2(selectedSubarea.getValue());
        request.setAreaLesaoN3(selectedEspecificacao.getValue());

        // Dados do usuário
        request.setMassa(massa.getValue());
        request.setAltura(altura.getValue());
        request.setGrauEscalada(grauEscalada.getValue());

        // Dados de prática
        request.setTempoPraticaMeses(tempoPraticaMeses.getValue());
        request.setFrequenciaSemanal(frequenciaSemanal.getValue());
        request.setHorasSemanais(horasSemanais.getValue());
        request.setLesoesPrevias(lesoesPrevias.getValue());

        // Dados de lesão
        request.setReincidencia(reincidencia.getValue());
        request.setBuscouAtendimento(buscouAtendimento.getValue());

        if (buscouAtendimento.getValue()) {
            request.setProfissionalAtendimento(profissionalAtendimento.getValue());
            request.setDiagnostico(diagnostico.getValue());
            request.setProfissionalTratamento(profissionalTratamento.getValue());
            request.setModalidadePraticada(modalidadePraticada.getValue());
        }

        lesaoRepository.saveLesao(request).observeForever(response -> {
            isLoading.setValue(false);

            if (response != null && response.isSuccess()) {
                uiEvent.setValue(Event.SHOW_SUCCESS_MESSAGE);
            } else {
                uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            }
        });
    }

    // Método para obter o ID do usuário atual
    private int getCurrentUserId() {
        // Obter o ID do usuário atual do AuthRepository
        return authRepository.getCurrentUserId();
    }
}
