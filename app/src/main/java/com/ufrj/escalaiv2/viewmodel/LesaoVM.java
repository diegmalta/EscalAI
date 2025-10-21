package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoRequest;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoResponse;
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
import com.ufrj.escalaiv2.EscalAIApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LesaoVM extends AndroidViewModel {
    private final LesaoRepository lesaoRepository;
    private final AuthRepository authRepository;
    private final UsuarioRepository usuarioRepository;
    private final ScheduledExecutorService executorService;

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

    // LiveData para previsão ML
    private final MutableLiveData<PrevisaoAfastamentoResponse> previsaoAfastamento;
    private final MutableLiveData<Boolean> isPredicting;
    private final MutableLiveData<String> intervaloConfiancaTexto;

    // ID da lesão atual (para previsão ML)
    private final MutableLiveData<Integer> lesaoId;

    // LiveData para datas da lesão
    private final MutableLiveData<String> dataInicio;
    private final MutableLiveData<String> dataConclusao;
    private final MutableLiveData<Boolean> canConcludeLesao;

    // Dados para os dropdowns de área da lesão
    private final List<String> areas;
    private final Map<Integer, List<String>> subareas;
    private final Map<String, List<String>> especificacoes;

    public LesaoVM(Application application) {
        super(application);
        lesaoRepository = new LesaoRepository(application);
        authRepository = new AuthRepository(application);
        usuarioRepository = new UsuarioRepository(application);
        executorService = Executors.newScheduledThreadPool(1);

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

        // Inicializar os LiveData para previsão ML
        previsaoAfastamento = new MutableLiveData<>();
        isPredicting = new MutableLiveData<>(false);
        intervaloConfiancaTexto = new MutableLiveData<>("");

        // Inicializar lesaoId
        lesaoId = new MutableLiveData<>(-1);

        // Inicializar os LiveData para datas da lesão
        dataInicio = new MutableLiveData<>("");
        dataConclusao = new MutableLiveData<>("");
        canConcludeLesao = new MutableLiveData<>(false);

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

        // Carregar dados do usuário apenas quando necessário
        // loadUserData();
    }

    // Método para carregar dados básicos do usuário
    public void loadUserBasicData() {
        android.util.Log.d("LesaoVM", "Carregando dados básicos do usuário via API");

        AuthRepository authRepository = new AuthRepository(EscalAIApplication.getAppContext());
        authRepository.getUserProfile().observeForever(userProfile -> {
            if (userProfile != null) {
                android.util.Log.d("LesaoVM", "Dados do usuário carregados com sucesso");

                // Atualizar massa e altura
                if (userProfile.getPeso() != null) {
                    massa.postValue(userProfile.getPeso());
                }

                if (userProfile.getAltura() != null) {
                    altura.postValue(userProfile.getAltura());
                }

                // Grau de escalada - só preencher se houver dados válidos
                Integer grauId = userProfile.getGrauEscaladaRedpoint();
                if (grauId != null && grauId > 0) {
                    grauEscalada.postValue(grauId);
                    GrauEscaladaBrasileiro grau = GrauEscaladaBrasileiro.getById(grauId);
                    if (grau != null) {
                        grauEscaladaText.postValue(grau.getNome());
                    }
                } else {
                    // Deixar vazio se não houver dados válidos
                    grauEscalada.postValue(0);
                    grauEscaladaText.postValue("");
                }
            } else {
                android.util.Log.e("LesaoVM", "Erro ao carregar dados do usuário");
            }
        });
    }

    private void loadUserData() {
        authRepository.getCurrentUserIdAsync(currentUserId -> {
            isLoading.postValue(true);

            // Buscar dados do usuário no banco local usando LiveData
            LiveData<Usuario> usuarioLiveData = usuarioRepository.getUsuario(currentUserId);

            // Observar o LiveData para obter os dados do usuário quando disponíveis
            usuarioLiveData.observeForever(new Observer<Usuario>() {
                @Override
                public void onChanged(Usuario usuario) {
                    // Remover o observer após obter os dados para evitar vazamentos de memória
                    usuarioLiveData.removeObserver(this);

                    if (usuario != null) {
                        massa.postValue(usuario.getPeso());
                        altura.postValue(usuario.getAltura());

                        // Grau de escalada
                        int grauId = usuario.getGrauEscaladaRedpoint();
                        grauEscalada.postValue(grauId);
                        GrauEscaladaBrasileiro grau = GrauEscaladaBrasileiro.getById(grauId);
                        grauEscaladaText.postValue(grau.getNome());
                    }

                    // Buscar dados de lesão na API
                    lesaoRepository.getUserLesoes().observeForever(response -> {
                        isLoading.postValue(false);

                        if (response != null && response.isSuccess() && response.getData() != null) {
                            LesaoResponse.LesaoData data = response.getData();

                            // Área da lesão
                            int areaId = data.getAreaLesaoN1();
                            selectedArea.postValue(areaId);
                            if (areaId >= 0 && areaId < areas.size()) {
                                selectedAreaText.postValue(areas.get(areaId));
                                subareaEnabled.postValue(true);

                                int subareaId = data.getAreaLesaoN2();
                                if (subareaId >= 0 && subareaId < subareas.get(areaId).size()) {
                                    String subareaText = subareas.get(areaId).get(subareaId);
                                    selectedSubarea.postValue(subareaId);
                                    selectedSubareaText.postValue(subareaText);
                                    especificacaoEnabled.postValue(true);

                                    int especificacaoId = data.getAreaLesaoN3();
                                    if (especificacaoId >= 0 && especificacaoId < especificacoes.get(subareaText).size()) {
                                        selectedEspecificacao.postValue(especificacaoId);
                                        selectedEspecificacaoText.postValue(especificacoes.get(subareaText).get(especificacaoId));
                                    }
                                }
                            }

                            // Dados de prática
                            tempoPraticaMeses.postValue(data.getTempoPraticaMeses());
                            frequenciaSemanal.postValue(data.getFrequenciaSemanal());
                            horasSemanais.postValue(data.getHorasSemanais());
                            lesoesPrevias.postValue(data.getLesoesPrevias());

                            // Dados de lesão
                            reincidencia.postValue(data.isReincidencia());
                            buscouAtendimento.postValue(data.isBuscouAtendimento());

                            if (data.isBuscouAtendimento()) {
                                atendimentoFieldsEnabled.postValue(true);

                                // Profissional de atendimento
                                int profAtendId = data.getProfissionalAtendimento();
                                profissionalAtendimento.postValue(profAtendId);
                                ProfissionalSaude profAtend = ProfissionalSaude.getById(profAtendId);
                                profissionalAtendimentoText.postValue(profAtend.getNome());

                                // Diagnóstico
                                int diagId = data.getDiagnostico();
                                diagnostico.postValue(diagId);
                                DiagnosticoLesao diag = DiagnosticoLesao.getById(diagId);
                                diagnosticoText.postValue(diag.getNome());

                                // Profissional de tratamento
                                int profTratId = data.getProfissionalTratamento();
                                profissionalTratamento.postValue(profTratId);
                                ProfissionalSaude profTrat = ProfissionalSaude.getById(profTratId);
                                profissionalTratamentoText.postValue(profTrat.getNome());

                                // Modalidade praticada
                                int modalId = data.getModalidadePraticada();
                                modalidadePraticada.postValue(modalId);
                                TipoTreino modal = TipoTreino.getById(modalId);
                                modalidadePraticadaText.postValue(modal.getNome());
                            }
                        }
                    });
                }
            });
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

    // Getters para previsão ML
    public LiveData<PrevisaoAfastamentoResponse> getPrevisaoAfastamento() {
        return previsaoAfastamento;
    }

    public LiveData<String> getIntervaloConfiancaTexto() {
        return intervaloConfiancaTexto;
    }

    private final MutableLiveData<String> tempoRecuperacaoFormatado = new MutableLiveData<>("");

    public LiveData<String> getTempoRecuperacaoFormatado() {
        return tempoRecuperacaoFormatado;
    }

    private final MutableLiveData<String> mensagemComConfianca = new MutableLiveData<>("");

    public LiveData<String> getMensagemComConfianca() {
        return mensagemComConfianca;
    }

    private String formatarIntervaloMeses(double min, double max) {
        double minMeses = arredondarParaMeioMes(min);
        double maxMeses = arredondarParaMeioMes(max);

        String minStr = formatarMeses(minMeses);
        String maxStr = formatarMeses(maxMeses);

        if (minMeses == maxMeses) {
            return minStr + " meses";
        } else {
            return minStr + " - " + maxStr + " meses";
        }
    }

    private double arredondarParaMeioMes(double valor) {
        return Math.round(valor * 2.0) / 2.0;
    }

    private String formatarMeses(double meses) {
        if (meses == Math.floor(meses)) {
            return String.valueOf((int) meses);
        } else {
            return String.format(Locale.US, "%.1f", meses);
        }
    }

    private String converterMesesParaMesesEDias(double meses) {
        int mesesInteiros = (int) meses;
        double parteDecimal = meses - mesesInteiros;

        int dias = (int) Math.round(parteDecimal * 30);

        if (mesesInteiros == 0) {
            return dias + " dias";
        } else if (dias == 0) {
            return mesesInteiros + " meses";
        } else {
            return mesesInteiros + " meses e " + dias + " dias";
        }
    }



    public LiveData<Boolean> getIsPredicting() {
        return isPredicting;
    }

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

    // Métodos para atualizar datas
    public void updateDataInicio(String data) {
        dataInicio.setValue(data);
        validateConcludeButton();
    }

    public void updateDataConclusao(String data) {
        dataConclusao.setValue(data);
        validateConcludeButton();
    }

    // Método para validar se pode concluir a lesão
    private void validateConcludeButton() {
        boolean canConclude = validateRequiredFieldsForConclusion();
        canConcludeLesao.setValue(canConclude);
    }

    // Atualiza o ID da lesão corrente
    public void setLesaoId(int id) {
        lesaoId.setValue(id);
    }

    public LiveData<Integer> getLesaoId() {
        return lesaoId;
    }

    // Getters para datas da lesão
    public LiveData<String> getDataInicio() {
        return dataInicio;
    }

    public LiveData<String> getDataConclusao() {
        return dataConclusao;
    }

    public LiveData<Boolean> getCanConcludeLesao() {
        return canConcludeLesao;
    }

    // Método para salvar os dados
    public void saveLesaoData() {
        // Validar apenas campos essenciais para salvar
        if (!validateRequiredFields()) {
            android.util.Log.d("LesaoVM", "Validação falhou ao salvar lesão");
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            return;
        }

        android.util.Log.d("LesaoVM", "Iniciando salvamento da lesão");
        isLoading.setValue(true);

        LesaoRequest request = new LesaoRequest();

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

        // Dados de data da lesão
        String dataInicioFormatted = null;
        String dataConclusaoFormatted = null;

        if (dataInicio.getValue() != null && !dataInicio.getValue().trim().isEmpty()) {
            dataInicioFormatted = convertDateFormat(dataInicio.getValue());
        }

        if (dataConclusao.getValue() != null && !dataConclusao.getValue().trim().isEmpty()) {
            dataConclusaoFormatted = convertDateFormat(dataConclusao.getValue());
        }

        request.setDataInicio(dataInicioFormatted);
        request.setDataConclusao(dataConclusaoFormatted);

        // Log para debug
        android.util.Log.d("LesaoVM", "Dados de data sendo enviados:");
        android.util.Log.d("LesaoVM", "dataInicio: " + dataInicioFormatted);
        android.util.Log.d("LesaoVM", "dataConclusao: " + dataConclusaoFormatted);

        // Log para campos obrigatórios
        android.util.Log.d("LesaoVM", "Campos obrigatórios:");
        android.util.Log.d("LesaoVM", "areaLesaoN1: " + selectedArea.getValue());
        android.util.Log.d("LesaoVM", "areaLesaoN2: " + selectedSubarea.getValue());
        android.util.Log.d("LesaoVM", "areaLesaoN3: " + selectedEspecificacao.getValue());
        android.util.Log.d("LesaoVM", "massa: " + massa.getValue());
        android.util.Log.d("LesaoVM", "altura: " + altura.getValue());
        android.util.Log.d("LesaoVM", "grauEscalada: " + grauEscalada.getValue());

        Integer currentId = lesaoId.getValue();
        if (currentId != null && currentId > 0) {
            // Garantir que o ID da lesão está sendo enviado na requisição
            request.setId(currentId);
            lesaoRepository.updateLesao(currentId, request).observeForever(response -> {
                isLoading.postValue(false);
                if (response != null && response.isSuccess()) {
                    uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                    validateConcludeButton(); // Revalidar botão após salvar
                } else {
                    uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                }
            });
        } else {
            lesaoRepository.saveLesao(request).observeForever(response -> {
                isLoading.postValue(false);
                if (response != null && response.isSuccess()) {
                    uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                    validateConcludeButton(); // Revalidar botão após salvar
                } else {
                    uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                }
            });
        }
    }

    // Método para concluir a lesão
    public void concludeLesao() {
        // Verificar se pode concluir
        if (!canConcludeLesao.getValue()) {
            return;
        }

        // Validar campos obrigatórios para conclusão
        if (!validateRequiredFieldsForConclusion()) {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            return;
        }

        // Salvar a lesão com a data de conclusão
        saveLesaoData();
    }

    // Método para prever tempo de afastamento
    public void preverTempoAfastamento() {
        // Validar campos obrigatórios para previsão
        if (!validateFieldsForPrediction()) {
            android.util.Log.d("LesaoVM", "Validação falhou ao prever tempo de afastamento");
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
            return;
        }

        android.util.Log.d("LesaoVM", "Iniciando previsão de tempo de afastamento");
        isPredicting.setValue(true);
        previsaoAfastamento.setValue(null);

        int currentLesaoId = lesaoId.getValue() != null ? lesaoId.getValue() : 0;
        PrevisaoAfastamentoRequest request = new PrevisaoAfastamentoRequest(
            currentLesaoId,
            buscouAtendimento.getValue() != null ? buscouAtendimento.getValue() : false,
            reincidencia.getValue() != null ? reincidencia.getValue() : false,
            lesoesPrevias.getValue() != null ? lesoesPrevias.getValue() : 0
        );

        lesaoRepository.preverAfastamento(request).observeForever(response -> {
            isPredicting.postValue(false);

            if (response != null) {
                previsaoAfastamento.postValue(response);

                // Os valores vêm em meses do servidor
                double min = response.getIntervaloConfiancaMin();
                double max = response.getIntervaloConfiancaMax();

                // Formatar o intervalo em meses com valores decimais (1, 1.5, 2.0, etc.)
                String intervaloMeses = formatarIntervaloMeses(min, max);
                tempoRecuperacaoFormatado.postValue(intervaloMeses);
                intervaloConfiancaTexto.postValue(intervaloMeses);

                // Combinar a mensagem original com a confiança
                int confiancaPercent = (int) (response.getConfianca() * 100);
                String mensagem = response.getMessage() + " (Confiança: " + confiancaPercent + "%)";
                mensagemComConfianca.postValue(mensagem);
            } else {
                // Em caso de erro, não gerar previsão; sinalizar erro para UI
                uiEvent.postValue(Event.PREDICTION_ERROR);
            }
        });

        // Timeout de 30 segundos
        executorService.schedule(() -> {
            if (isPredicting.getValue()) {
                isPredicting.postValue(false);
                // Não gerar previsão em timeout; sinalizar erro para UI
                uiEvent.postValue(Event.PREDICTION_ERROR);
            }
        }, 30, TimeUnit.SECONDS);
    }

    // Método para obter o ID do usuário atual
    private void getCurrentUserIdAsync(java.util.function.Consumer<Integer> callback) {
        // Obter o ID do usuário atual do AuthRepository
        authRepository.getCurrentUserIdAsync(callback);
    }

    // Método para carregar uma lesão específica
    public void loadLesaoById(int lesaoId) {
        isLoading.setValue(true);

        lesaoRepository.getLesaoById(lesaoId).observeForever(response -> {
            isLoading.postValue(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                LesaoResponse.LesaoData lesao = response.getData();
                populateFieldsWithLesaoData(lesao);
            }
        });
    }

    // Método para preencher os campos com os dados da lesão
    private void populateFieldsWithLesaoData(LesaoResponse.LesaoData lesao) {
        // Guardar ID da lesão para uso na previsão ML
        lesaoId.postValue(lesao.getId());
        // Área da lesão
        selectedArea.postValue(lesao.getAreaLesaoN1());
        selectedAreaText.postValue(areas.get(lesao.getAreaLesaoN1()));

        if (lesao.getAreaLesaoN2() >= 0) {
            selectedSubarea.postValue(lesao.getAreaLesaoN2());
            List<String> subareasList = subareas.get(lesao.getAreaLesaoN1());
            if (subareasList != null && lesao.getAreaLesaoN2() < subareasList.size()) {
                selectedSubareaText.postValue(subareasList.get(lesao.getAreaLesaoN2()));
            }
            subareaEnabled.postValue(true);
        }

        if (lesao.getAreaLesaoN3() >= 0) {
            selectedEspecificacao.postValue(lesao.getAreaLesaoN3());
            String subareaKey = selectedSubareaText.getValue();
            List<String> especificacoesList = especificacoes.get(subareaKey);
            if (especificacoesList != null && lesao.getAreaLesaoN3() < especificacoesList.size()) {
                selectedEspecificacaoText.postValue(especificacoesList.get(lesao.getAreaLesaoN3()));
            }
            especificacaoEnabled.postValue(true);
        }

        // Dados do usuário - priorizar dados da lesão, usar dados do usuário como fallback
        if (lesao.getMassa() > 0) {
            massa.postValue((double) lesao.getMassa());
        } else {
            // Se não há massa na lesão, carregar do perfil do usuário
            loadUserBasicData();
        }

        if (lesao.getAltura() > 0) {
            altura.postValue(lesao.getAltura());
        } else {
            // Se não há altura na lesão, carregar do perfil do usuário
            loadUserBasicData();
        }

        // Grau de escalada - tratar valores inválidos
        int grauId = lesao.getGrauEscalada();
        if (grauId >= 0 && grauId < GrauEscaladaBrasileiro.getAllNames().length) {
            grauEscalada.postValue(grauId);
            grauEscaladaText.postValue(GrauEscaladaBrasileiro.getAllNames()[grauId]);
        } else {
            grauEscalada.postValue(0);
            grauEscaladaText.postValue("");
        }

        // Dados de prática
        tempoPraticaMeses.postValue(lesao.getTempoPraticaMeses());
        frequenciaSemanal.postValue(lesao.getFrequenciaSemanal());
        horasSemanais.postValue(lesao.getHorasSemanais());
        lesoesPrevias.postValue(lesao.getLesoesPrevias());

        // Dados de lesão
        reincidencia.postValue(lesao.isReincidencia());
        buscouAtendimento.postValue(lesao.isBuscouAtendimento());
        atendimentoFieldsEnabled.postValue(lesao.isBuscouAtendimento());

        if (lesao.isBuscouAtendimento()) {
            profissionalAtendimento.postValue(lesao.getProfissionalAtendimento());
            profissionalAtendimentoText.postValue(ProfissionalSaude.getAllNames()[lesao.getProfissionalAtendimento()]);

            diagnostico.postValue(lesao.getDiagnostico());
            diagnosticoText.postValue(DiagnosticoLesao.getAllNames()[lesao.getDiagnostico()]);

            profissionalTratamento.postValue(lesao.getProfissionalTratamento());
            profissionalTratamentoText.postValue(ProfissionalSaude.getAllNames()[lesao.getProfissionalTratamento()]);

            modalidadePraticada.postValue(lesao.getModalidadePraticada());
            modalidadePraticadaText.postValue(TipoTreino.getAllNames()[lesao.getModalidadePraticada()]);
        }

        // Dados de data da lesão
        dataInicio.postValue(convertServerDateToDisplay(lesao.getDataInicio()));
        dataConclusao.postValue(convertServerDateToDisplay(lesao.getDataConclusao()));
        validateConcludeButton();
    }

    // Método para validar campos obrigatórios
    private boolean validateRequiredFields() {
        boolean isValid = true;

        // Validar apenas campos essenciais para salvar
        if (selectedArea.getValue() == null || selectedArea.getValue() == -1) {
            android.util.Log.d("LesaoVM", "Validação falhou: área não selecionada");
            isValid = false;
        }
        if (selectedSubarea.getValue() == null || selectedSubarea.getValue() == -1) {
            android.util.Log.d("LesaoVM", "Validação falhou: subárea não selecionada");
            isValid = false;
        }
        if (selectedEspecificacao.getValue() == null || selectedEspecificacao.getValue() == -1) {
            android.util.Log.d("LesaoVM", "Validação falhou: especificação não selecionada");
            isValid = false;
        }
        if (massa.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: massa é null");
            isValid = false;
        }
        if (altura.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: altura é null");
            isValid = false;
        }
        if (grauEscalada.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: grau de escalada é null");
            isValid = false;
        }
        if (tempoPraticaMeses.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: tempo de prática é null");
            isValid = false;
        }
        if (frequenciaSemanal.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: frequência semanal é null");
            isValid = false;
        }
        if (horasSemanais.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: horas semanais é null");
            isValid = false;
        }
        if (lesoesPrevias.getValue() == null) {
            android.util.Log.d("LesaoVM", "Validação falhou: lesões prévias é null");
            isValid = false;
        }

        // Data de início é opcional para salvar, obrigatória apenas para concluir
        // Data de conclusão é opcional para salvar, obrigatória apenas para concluir

        android.util.Log.d("LesaoVM", "Validação concluída: " + (isValid ? "SUCESSO" : "FALHA"));
        return isValid;
    }

    // Método para validar campos obrigatórios para conclusão
    private boolean validateRequiredFieldsForConclusion() {
        boolean isValid = validateRequiredFields();

        // Para concluir, a data de início é obrigatória
        if (dataInicio.getValue() == null || dataInicio.getValue().trim().isEmpty()) {
            isValid = false;
        }

        // Para concluir, a data de conclusão é obrigatória
        if (dataConclusao.getValue() == null || dataConclusao.getValue().trim().isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    // Método para validar campos para previsão (sem exigir data de conclusão)
    private boolean validateFieldsForPrediction() {
        boolean isValid = true;

        if (selectedArea.getValue() == null || selectedArea.getValue() == -1) {
            isValid = false;
        }
        if (selectedSubarea.getValue() == null || selectedSubarea.getValue() == -1) {
            isValid = false;
        }
        if (selectedEspecificacao.getValue() == null || selectedEspecificacao.getValue() == -1) {
            isValid = false;
        }
        if (massa.getValue() == null) {
            isValid = false;
        }
        if (altura.getValue() == null) {
            isValid = false;
        }
        if (grauEscalada.getValue() == null) {
            isValid = false;
        }
        if (tempoPraticaMeses.getValue() == null) {
            isValid = false;
        }
        if (frequenciaSemanal.getValue() == null) {
            isValid = false;
        }
        if (horasSemanais.getValue() == null) {
            isValid = false;
        }
        if (lesoesPrevias.getValue() == null) {
            isValid = false;
        }
        // Data de início não é obrigatória para previsão

        return isValid;
    }

    // Método para converter formato de data de DD/MM/YYYY para YYYY-MM-DD
    private String convertDateFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return "";
        }

        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

            java.util.Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (java.text.ParseException e) {
            android.util.Log.e("LesaoVM", "Erro ao converter data: " + dateString, e);
            return "";
        }
    }

    // Método para converter formato de data do servidor (YYYY-MM-DD) para exibição (DD/MM/YYYY)
    public String convertServerDateToDisplay(String serverDate) {
        if (serverDate == null || serverDate.trim().isEmpty()) {
            return "";
        }

        try {
            java.text.SimpleDateFormat serverFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());

            java.util.Date date = serverFormat.parse(serverDate);
            return displayFormat.format(date);
        } catch (java.text.ParseException e) {
            android.util.Log.e("LesaoVM", "Erro ao converter data do servidor: " + serverDate, e);
            return serverDate; // Retorna o valor original se não conseguir converter
        }
    }
}
