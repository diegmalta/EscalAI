package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.Slider;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivityTreinoBinding;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.enums.TipoTreino;
import com.ufrj.escalaiv2.viewmodel.TreinoVM;

import java.util.Map;

public class TreinoActivity extends AppCompatActivity {

    private ActivityTreinoBinding binding;
    private TreinoVM viewModel;
    private ArrayAdapter<String> dropdownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_treino);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(TreinoVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupToolbar();
        setupDropdown();
        setupSlider();
        setupObservers();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDropdown() {
        // Criar array de strings com os nomes dos tipos de treino
        String[] tipoTreinoNames = new String[TipoTreino.values().length];
        for (int i = 0; i < TipoTreino.values().length; i++) {
            tipoTreinoNames[i] = TipoTreino.values()[i].getNome();
        }

        // Configurar adapter para o dropdown
        dropdownAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                tipoTreinoNames
        );

        binding.tipoTreinoDropdown.setAdapter(dropdownAdapter);

        // Configurar listener para seleção no dropdown
        binding.tipoTreinoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.updateSelectedTreinoTipo(position);
        });
    }

    private void setupSlider() {
        binding.duracaoSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    viewModel.updateDuracaoTreino(value);
                }
            }
        });

        // Configurar formato do label do slider
        binding.duracaoSlider.setLabelFormatter(value -> {
            if (value == 1.0f) {
                return "1 hora";
            } else {
                return String.format("%.1f horas", value);
            }
        });
    }

    private void setupObservers() {
        // Observer para eventos da UI
        viewModel.getUiEvent().observe(this, event -> {
            if (event != null) {
                handleUiEvent(event);
            }
        });

        // Observer para o resumo de treinos
        viewModel.getResumoTreinosHoje().observe(this, resumoMap -> {
            updateResumoTable(resumoMap);
        });

        // Observer para o tipo de treino selecionado
        viewModel.getSelectedTreinoTipo().observe(this, position -> {
            if (position != null && position >= 0) {
                binding.tipoTreinoDropdown.setText(
                        TipoTreino.values()[position].getNome(),
                        false
                );
            }
        });
    }

    private void setupClickListeners() {
        binding.saveButton.setOnClickListener(v -> {
            viewModel.saveTreinoData();
        });
    }

    private void handleUiEvent(Event event) {
        switch (event) {
            case SHOW_SUCCESS_MESSAGE:
                Toast.makeText(this, "Atividade registrada com sucesso!", Toast.LENGTH_SHORT).show();
                // Limpar seleções após sucesso
                clearForm();
                break;
            case SHOW_ERROR_MESSAGE:
                Toast.makeText(this, "Erro ao registrar atividade. Verifique os dados.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void clearForm() {
        // Resetar dropdown
        binding.tipoTreinoDropdown.setText("", false);

        // Resetar slider para valor padrão
        binding.duracaoSlider.setValue(1.0f);

        // Atualizar ViewModel com reset
        viewModel.resetForm();
    }

    private void updateResumoTable(Map<String, Float> resumoMap) {
        TableLayout tableLayout = binding.resumoTable;

        // Remover todas as linhas exceto o header (primeira linha)
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        if (resumoMap == null || resumoMap.isEmpty()) {
            // Mostrar estado vazio
            binding.resumoCard.setVisibility(View.GONE);
            binding.emptyStateText.setVisibility(View.VISIBLE);
        } else {
            // Mostrar tabela com dados
            binding.resumoCard.setVisibility(View.VISIBLE);
            binding.emptyStateText.setVisibility(View.GONE);

            // Adicionar linhas para cada tipo de treino
            for (Map.Entry<String, Float> entry : resumoMap.entrySet()) {
                addTableRow(tableLayout, entry.getKey(), entry.getValue());
            }
        }
    }

    private void addTableRow(TableLayout tableLayout, String tipoTreino, Float horas) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        row.setPadding(16, 12, 16, 12);

        // TextView para o tipo de treino
        TextView tipoTextView = new TextView(this);
        tipoTextView.setText(tipoTreino);
        tipoTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f));
        tipoTextView.setTextSize(16);

        // TextView para as horas
        TextView horasTextView = new TextView(this);
        String horasText = String.format("%.1fh", horas);
        horasTextView.setText(horasText);
        horasTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        horasTextView.setTextSize(16);
        horasTextView.setGravity(android.view.Gravity.END);

        // Adicionar TextViews à linha
        row.addView(tipoTextView);
        row.addView(horasTextView);

        // Adicionar linha à tabela
        tableLayout.addView(row);

        // Adicionar separador visual (linha divisória)
        if (tableLayout.getChildCount() > 2) {
            View divider = new View(this);
            divider.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    1
            ));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            tableLayout.addView(divider);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }
}