package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivityRegistrarLesaoBinding;
import com.ufrj.escalaiv2.enums.DiagnosticoLesao;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.enums.GrauEscaladaBrasileiro;
import com.ufrj.escalaiv2.enums.ProfissionalSaude;
import com.ufrj.escalaiv2.enums.TipoTreino;
import com.ufrj.escalaiv2.viewmodel.LesaoVM;
import com.ufrj.escalaiv2.dto.LesaoResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;

public class RegistrarLesaoActivity extends AppCompatActivity {
    private ActivityRegistrarLesaoBinding binding;
    private LesaoVM lesaoVM;
    private boolean shouldScrollToPrediction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o ViewModel
        lesaoVM = new ViewModelProvider(this).get(LesaoVM.class);

        // Configurar o DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registrar_lesao);
        binding.setViewModel(lesaoVM);
        binding.setLifecycleOwner(this);

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Configurar os dropdowns
        setupDropdowns();

        // Configurar os listeners
        setupListeners();

        // Verificar se há dados de lesão para edição
        checkForLesaoData();

        // Verificar se deve mostrar previsão automaticamente
        checkForPredictionRequest();

        // Observar eventos da UI
        observeUiEvents();

        // Carregar dados básicos do usuário (apenas se não houver dados de lesão)
        // Isso será feito após verificar se há dados de lesão para evitar carregamento desnecessário
        if (getIntent().getParcelableExtra("lesao_data") == null) {
            lesaoVM.loadUserBasicData();
        } else {
            // Se há dados de lesão, verificar se precisa carregar dados adicionais do usuário
            loadUserDataIfNeeded();
        }
    }

    private void checkForLesaoData() {
        LesaoResponse.LesaoData lesaoData = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            lesaoData = getIntent().getParcelableExtra("lesao_data", LesaoResponse.LesaoData.class);
        } else {
            lesaoData = getIntent().getParcelableExtra("lesao_data");
        }
        if (lesaoData != null) {
            // Preencher os campos com os dados da lesão
            populateFieldsWithLesaoData(lesaoData);
        }
    }

    private void populateFieldsWithLesaoData(LesaoResponse.LesaoData lesao) {
        // Armazena o ID da lesão no ViewModel para previsão ML
        lesaoVM.setLesaoId(lesao.getId());
        // Área da lesão
        int areaN1 = lesao.getAreaLesaoN1();
        if (areaN1 >= 0) {
            List<String> areas = lesaoVM.getAreas();
            if (areaN1 < areas.size()) {
                binding.areaDropdown.setText(areas.get(areaN1), false);
                lesaoVM.updateSelectedArea(areaN1);

                // Configurar subárea
                int areaN2 = lesao.getAreaLesaoN2();
                if (areaN2 >= 0) {
                    List<String> subareas = lesaoVM.getSubareas(areaN1);
                    if (subareas != null && areaN2 < subareas.size()) {
                        ArrayAdapter<String> subareaAdapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                subareas
                        );
                        binding.subareaDropdown.setAdapter(subareaAdapter);
                        binding.subareaDropdown.setText(subareaAdapter.getItem(areaN2), false);
                        lesaoVM.updateSelectedSubarea(areaN2);

                        // Configurar especificação
                        int areaN3 = lesao.getAreaLesaoN3();
                        if (areaN3 >= 0) {
                            String subareaText = subareas.get(areaN2);
                            List<String> especificacoes = lesaoVM.getEspecificacoes(subareaText);
                            if (especificacoes != null && areaN3 < especificacoes.size()) {
                                ArrayAdapter<String> especificacaoAdapter = new ArrayAdapter<>(
                                        this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        especificacoes
                                );
                                binding.especificacaoDropdown.setAdapter(especificacaoAdapter);
                                binding.especificacaoDropdown.setText(especificacaoAdapter.getItem(areaN3), false);
                                lesaoVM.updateSelectedEspecificacao(areaN3);
                            }
                        }
                    }
                }
            }
        }

        // Dados do usuário - priorizar dados da lesão
        if (lesao.getMassa() > 0) {
            binding.massaInput.setText(String.valueOf(lesao.getMassa()));
            lesaoVM.updateMassa(lesao.getMassa());
        }

        if (lesao.getAltura() > 0) {
            binding.alturaInput.setText(String.valueOf(lesao.getAltura()));
            lesaoVM.updateAltura(lesao.getAltura());
        }

        // Grau de escalada - tratar valores inválidos
        int grauId = lesao.getGrauEscalada();
        if (grauId >= 0 && grauId < GrauEscaladaBrasileiro.getAllNames().length) {
            binding.grauEscaladaDropdown.setText(GrauEscaladaBrasileiro.getAllNames()[grauId], false);
            lesaoVM.updateGrauEscalada(grauId);
        } else {
            binding.grauEscaladaDropdown.setText("", false);
            lesaoVM.updateGrauEscalada(0);
        }
        lesaoVM.updateMassa(lesao.getMassa());
        lesaoVM.updateAltura(lesao.getAltura());

        // Dados de prática
        binding.tempoPraticaInput.setText(String.valueOf(lesao.getTempoPraticaMeses()));
        binding.frequenciaInput.setText(String.valueOf(lesao.getFrequenciaSemanal()));
        binding.horasInput.setText(String.valueOf(lesao.getHorasSemanais()));
        binding.lesoesPreviasInput.setText(String.valueOf(lesao.getLesoesPrevias()));
        lesaoVM.updateTempoPraticaMeses(lesao.getTempoPraticaMeses());
        lesaoVM.updateFrequenciaSemanal(lesao.getFrequenciaSemanal());
        lesaoVM.updateHorasSemanais(lesao.getHorasSemanais());
        lesaoVM.updateLesoesPrevias(lesao.getLesoesPrevias());

        // Dados de lesão
        binding.reincidenciaSwitch.setChecked(lesao.isReincidencia());
        binding.buscouAtendimentoSwitch.setChecked(lesao.isBuscouAtendimento());
        lesaoVM.updateReincidencia(lesao.isReincidencia());
        lesaoVM.updateBuscouAtendimento(lesao.isBuscouAtendimento());

        if (lesao.isBuscouAtendimento()) {
            String[] profAtendNames = ProfissionalSaude.getAllNames();
            String[] diagnosticoNames = DiagnosticoLesao.getAllNames();
            String[] profTratNames = ProfissionalSaude.getAllNames();
            String[] modalidadeNames = TipoTreino.getAllNames();
            
            int profAtendId = lesao.getProfissionalAtendimento();
            if (profAtendId >= 0 && profAtendId < profAtendNames.length) {
                binding.profAtendimentoDropdown.setText(profAtendNames[profAtendId], false);
                lesaoVM.updateProfissionalAtendimento(profAtendId);
            }
            
            int diagnosticoId = lesao.getDiagnostico();
            if (diagnosticoId >= 0 && diagnosticoId < diagnosticoNames.length) {
                binding.diagnosticoDropdown.setText(diagnosticoNames[diagnosticoId], false);
                lesaoVM.updateDiagnostico(diagnosticoId);
            }
            
            int profTratId = lesao.getProfissionalTratamento();
            if (profTratId >= 0 && profTratId < profTratNames.length) {
                binding.profTratamentoDropdown.setText(profTratNames[profTratId], false);
                lesaoVM.updateProfissionalTratamento(profTratId);
            }
            
            int modalidadeId = lesao.getModalidadePraticada();
            if (modalidadeId >= 0 && modalidadeId < modalidadeNames.length) {
                binding.modalidadeDropdown.setText(modalidadeNames[modalidadeId], false);
                lesaoVM.updateModalidadePraticada(modalidadeId);
            }
        }

        // Dados de data da lesão
        String dataInicioDisplay = lesaoVM.convertServerDateToDisplay(lesao.getDataInicio());
        String dataConclusaoDisplay = lesaoVM.convertServerDateToDisplay(lesao.getDataConclusao());

        binding.dataInicioInput.setText(dataInicioDisplay);
        binding.dataConclusaoInput.setText(dataConclusaoDisplay);
        lesaoVM.updateDataInicio(dataInicioDisplay);
        lesaoVM.updateDataConclusao(dataConclusaoDisplay);

        // Carregar previsão existente se disponível
        if (lesao.getTempoAfastamentoMeses() != null && lesao.getTempoAfastamentoMeses() > 0) {
            shouldScrollToPrediction = true;
            loadExistingPrediction(lesao);
        }
    }

    private void loadExistingPrediction(LesaoResponse.LesaoData lesao) {
        // Criar um objeto PrevisaoAfastamentoResponse simulado com os dados salvos
        com.ufrj.escalaiv2.dto.PrevisaoAfastamentoResponse previsao = 
            new com.ufrj.escalaiv2.dto.PrevisaoAfastamentoResponse();
        
        // Converter meses para dias (aproximadamente 30 dias por mês)
        double tempoMeses = lesao.getTempoAfastamentoMeses();
        int tempoDias = (int) Math.round(tempoMeses * 30);
        
        previsao.setTempoAfastamentoDias(tempoDias);
        previsao.setTempoAfastamentoSemanas(tempoMeses * 4.33); // Aproximadamente
        previsao.setIntervaloConfiancaMin(lesao.getIntervaloConfiancaMin() != null ? 
            lesao.getIntervaloConfiancaMin() : 0.0);
        previsao.setIntervaloConfiancaMax(lesao.getIntervaloConfiancaMax() != null ? 
            lesao.getIntervaloConfiancaMax() : 0.0);
        previsao.setMessage("Previsão baseada em dados históricos");
        
        // Atualizar o ViewModel com a previsão existente
        lesaoVM.setPrevisaoAfastamento(previsao);
        
        // Formatar tempo estimado apenas em meses (com ponto decimal quando necessário)
        String tempoFormatado = lesaoVM.formatarMesesPublic(tempoMeses);
        lesaoVM.setTempoRecuperacaoFormatado(tempoFormatado);
        
        // Formatar intervalo de confiança
        if (lesao.getIntervaloConfiancaMin() != null && lesao.getIntervaloConfiancaMax() != null) {
            double min = lesao.getIntervaloConfiancaMin();
            double max = lesao.getIntervaloConfiancaMax();
            String intervaloMeses = lesaoVM.formatarIntervaloMeses(min, max);
            lesaoVM.setIntervaloConfiancaTexto(intervaloMeses);
        }
        
        // Fazer scroll até a seção de previsão após um pequeno delay
        binding.getRoot().postDelayed(() -> {
            scrollToPredictionSection();
        }, 300);
    }
    
    private android.widget.ScrollView findScrollView(android.view.View view) {
        if (view instanceof android.widget.ScrollView) {
            return (android.widget.ScrollView) view;
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                android.widget.ScrollView found = findScrollView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void loadUserDataIfNeeded() {
        // Se não há dados de massa ou altura, carregar do perfil do usuário
        // Usar post para garantir que seja executado após a UI estar pronta
        binding.getRoot().post(() -> {
            if (binding.massaInput.getText().toString().isEmpty() ||
                binding.alturaInput.getText().toString().isEmpty()) {
                lesaoVM.loadUserBasicData();
            }
        });
    }

    private void setupDropdowns() {
        // Área da lesão
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                lesaoVM.getAreas()
        );
        binding.areaDropdown.setAdapter(areaAdapter);

        // Grau de escalada
        ArrayAdapter<String> grauAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                GrauEscaladaBrasileiro.getAllNames()
        );
        binding.grauEscaladaDropdown.setAdapter(grauAdapter);

        // Profissional de atendimento
        ArrayAdapter<String> profAtendAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ProfissionalSaude.getAllNames()
        );
        binding.profAtendimentoDropdown.setAdapter(profAtendAdapter);

        // Diagnóstico
        ArrayAdapter<String> diagnosticoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                DiagnosticoLesao.getAllNames()
        );
        binding.diagnosticoDropdown.setAdapter(diagnosticoAdapter);

        // Profissional de tratamento
        ArrayAdapter<String> profTratAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ProfissionalSaude.getAllNames()
        );
        binding.profTratamentoDropdown.setAdapter(profTratAdapter);

        // Modalidade praticada
        ArrayAdapter<String> modalidadeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                TipoTreino.getAllNames()
        );
        binding.modalidadeDropdown.setAdapter(modalidadeAdapter);
    }

    private void setupListeners() {
        // Área da lesão
        binding.areaDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateSelectedArea(position);

            // Atualizar o adapter da subárea
            ArrayAdapter<String> subareaAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    lesaoVM.getSubareas(position)
            );
            binding.subareaDropdown.setAdapter(subareaAdapter);
            binding.subareaDropdown.setText("", false);
        });

        // Subárea da lesão
        binding.subareaDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateSelectedSubarea(position);

            // Atualizar o adapter da especificação
            String subareaText = binding.subareaDropdown.getText().toString();
            ArrayAdapter<String> especificacaoAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    lesaoVM.getEspecificacoes(subareaText)
            );
            binding.especificacaoDropdown.setAdapter(especificacaoAdapter);
            binding.especificacaoDropdown.setText("", false);
        });

        // Especificação da lesão
        binding.especificacaoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateSelectedEspecificacao(position);
        });

        // Grau de escalada
        binding.grauEscaladaDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateGrauEscalada(position);
        });

        // Profissional de atendimento
        binding.profAtendimentoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateProfissionalAtendimento(position);
        });

        // Diagnóstico
        binding.diagnosticoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateDiagnostico(position);
        });

        // Profissional de tratamento
        binding.profTratamentoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateProfissionalTratamento(position);
        });

        // Modalidade praticada
        binding.modalidadeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            lesaoVM.updateModalidadePraticada(position);
        });

        // Botão de salvar
        binding.saveButton.setOnClickListener(v -> {
            // Atualizar os valores dos campos de texto antes de salvar
            try {
                float massaValue = Float.parseFloat(binding.massaInput.getText().toString());
                lesaoVM.updateMassa(massaValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateMassa(0f);
            }

            try {
                int alturaValue = Integer.parseInt(binding.alturaInput.getText().toString());
                lesaoVM.updateAltura(alturaValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateAltura(0);
            }

            try {
                int tempoPraticaValue = Integer.parseInt(binding.tempoPraticaInput.getText().toString());
                lesaoVM.updateTempoPraticaMeses(tempoPraticaValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateTempoPraticaMeses(0);
            }

            try {
                int frequenciaValue = Integer.parseInt(binding.frequenciaInput.getText().toString());
                lesaoVM.updateFrequenciaSemanal(frequenciaValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateFrequenciaSemanal(0);
            }

            try {
                int horasValue = Integer.parseInt(binding.horasInput.getText().toString());
                lesaoVM.updateHorasSemanais(horasValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateHorasSemanais(0);
            }

            try {
                int lesoesPreviasValue = Integer.parseInt(binding.lesoesPreviasInput.getText().toString());
                lesaoVM.updateLesoesPrevias(lesoesPreviasValue);
            } catch (NumberFormatException e) {
                lesaoVM.updateLesoesPrevias(0);
            }

            // Salvar os dados
            lesaoVM.saveLesaoData();
        });

        // Switch de buscou atendimento
        binding.buscouAtendimentoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lesaoVM.updateBuscouAtendimento(isChecked);
        });

        // Switch de reincidência
        binding.reincidenciaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lesaoVM.updateReincidencia(isChecked);
        });

        // Campos de data
        binding.dataInicioInput.setOnClickListener(v -> {
            showDatePickerDialog(binding.dataInicioInput, lesaoVM.getDataInicio().getValue());
        });

        binding.dataConclusaoInput.setOnClickListener(v -> {
            showDatePickerDialog(binding.dataConclusaoInput, lesaoVM.getDataConclusao().getValue());
        });

        // Botão de previsão ML
        binding.predictButton.setOnClickListener(v -> {
            lesaoVM.preverTempoAfastamento();
        });

        // Botão de concluir lesão
        binding.concluirButton.setOnClickListener(v -> {
            lesaoVM.concludeLesao();
        });
    }

    private void observeUiEvents() {
        lesaoVM.getUiEvent().observe(this, event -> {
            if (event == Event.SHOW_SUCCESS_MESSAGE) {
                Snackbar.make(
                        binding.getRoot(),
                        "Dados da lesão salvos com sucesso!",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();

                // Fechar a tela após salvar com sucesso
                finish();
            } else if (event == Event.SHOW_ERROR_MESSAGE) {
                Snackbar.make(
                        binding.getRoot(),
                        "Erro ao salvar dados. Verifique os campos e tente novamente.",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            } else if (event == Event.PREDICTION_ERROR) {
                Snackbar.make(
                        binding.getRoot(),
                        "Não foi possível gerar a previsão. Verifique sua conexão e tente novamente.",
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void checkForPredictionRequest() {
        boolean showPrediction = getIntent().getBooleanExtra("show_prediction", false);
        if (showPrediction) {
            shouldScrollToPrediction = true;
            // Aguardar um pouco para os dados carregarem e fazer scroll
            binding.getRoot().postDelayed(() -> {
                scrollToPredictionSection();
                // Gerar nova previsão após um pequeno delay adicional
                binding.getRoot().postDelayed(() -> {
                    lesaoVM.preverTempoAfastamento();
                }, 300);
            }, 500);
        }
    }
    
    private void scrollToPredictionSection() {
        if (!shouldScrollToPrediction) return;
        
        binding.secaoPrevisaoML.post(() -> {
            android.view.View rootView = binding.getRoot();
            android.widget.ScrollView scrollView = null;
            
            // Procurar ScrollView recursivamente
            if (rootView instanceof android.widget.ScrollView) {
                scrollView = (android.widget.ScrollView) rootView;
            } else {
                scrollView = findScrollView(rootView);
            }
            
            if (scrollView != null) {
                // Usar getTop() relativo ao ScrollView
                int scrollY = binding.secaoPrevisaoML.getTop() - 200;
                scrollView.smoothScrollTo(0, Math.max(0, scrollY));
                // Marcar que já fez scroll para evitar scrolls repetidos
                shouldScrollToPrediction = false;
            }
        });
    }

    private void showDatePickerDialog(com.google.android.material.textfield.TextInputEditText editText, String currentDate) {
        Calendar calendar = Calendar.getInstance();

        // Se há uma data atual, parse ela
        if (currentDate != null && !currentDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(currentDate);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                // Se não conseguir fazer parse, usar data atual
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(selectedCalendar.getTime());

                editText.setText(formattedDate);

                // Atualizar o ViewModel
                if (editText == binding.dataInicioInput) {
                    lesaoVM.updateDataInicio(formattedDate);
                } else if (editText == binding.dataConclusaoInput) {
                    lesaoVM.updateDataConclusao(formattedDate);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}