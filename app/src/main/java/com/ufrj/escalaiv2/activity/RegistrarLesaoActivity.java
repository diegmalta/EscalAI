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

public class RegistrarLesaoActivity extends AppCompatActivity {
    private ActivityRegistrarLesaoBinding binding;
    private LesaoVM lesaoVM;

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
        if (lesao.getAreaLesaoN1() >= 0) {
            binding.areaDropdown.setText(lesaoVM.getAreas().get(lesao.getAreaLesaoN1()), false);
            lesaoVM.updateSelectedArea(lesao.getAreaLesaoN1());

            // Configurar subárea
            if (lesao.getAreaLesaoN2() >= 0) {
                ArrayAdapter<String> subareaAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        lesaoVM.getSubareas(lesao.getAreaLesaoN1())
                );
                binding.subareaDropdown.setAdapter(subareaAdapter);
                binding.subareaDropdown.setText(subareaAdapter.getItem(lesao.getAreaLesaoN2()), false);
                lesaoVM.updateSelectedSubarea(lesao.getAreaLesaoN2());

                // Configurar especificação
                if (lesao.getAreaLesaoN3() >= 0) {
                    String subareaText = lesaoVM.getSubareas(lesao.getAreaLesaoN1()).get(lesao.getAreaLesaoN2());
                    ArrayAdapter<String> especificacaoAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            lesaoVM.getEspecificacoes(subareaText)
                    );
                    binding.especificacaoDropdown.setAdapter(especificacaoAdapter);
                    binding.especificacaoDropdown.setText(especificacaoAdapter.getItem(lesao.getAreaLesaoN3()), false);
                    lesaoVM.updateSelectedEspecificacao(lesao.getAreaLesaoN3());
                }
            }
        }

        // Dados do usuário
        binding.massaInput.setText(String.valueOf(lesao.getMassa()));
        binding.alturaInput.setText(String.valueOf(lesao.getAltura()));
        binding.grauEscaladaDropdown.setText(GrauEscaladaBrasileiro.getAllNames()[lesao.getGrauEscalada()], false);
        lesaoVM.updateMassa(lesao.getMassa());
        lesaoVM.updateAltura(lesao.getAltura());
        lesaoVM.updateGrauEscalada(lesao.getGrauEscalada());

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
            binding.profAtendimentoDropdown.setText(ProfissionalSaude.getAllNames()[lesao.getProfissionalAtendimento()], false);
            binding.diagnosticoDropdown.setText(DiagnosticoLesao.getAllNames()[lesao.getDiagnostico()], false);
            binding.profTratamentoDropdown.setText(ProfissionalSaude.getAllNames()[lesao.getProfissionalTratamento()], false);
            binding.modalidadeDropdown.setText(TipoTreino.getAllNames()[lesao.getModalidadePraticada()], false);
            lesaoVM.updateProfissionalAtendimento(lesao.getProfissionalAtendimento());
            lesaoVM.updateDiagnostico(lesao.getDiagnostico());
            lesaoVM.updateProfissionalTratamento(lesao.getProfissionalTratamento());
            lesaoVM.updateModalidadePraticada(lesao.getModalidadePraticada());
        }
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

        // Botão de previsão ML
        binding.predictButton.setOnClickListener(v -> {
            lesaoVM.preverTempoAfastamento();
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
            // Aguardar um pouco para os dados carregarem
            binding.getRoot().postDelayed(() -> {
                lesaoVM.preverTempoAfastamento();
            }, 1000);
        }
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