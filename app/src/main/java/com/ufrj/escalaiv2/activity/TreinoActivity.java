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
        setupDurationPickers();
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

    private void setupDurationPickers() {
        // Horas: 0 a 24
        binding.hourPicker.setMinValue(0);
        binding.hourPicker.setMaxValue(24);
        binding.hourPicker.setWrapSelectorWheel(false);
        binding.hourPicker.setFormatter(value -> String.format("%02d", value));

        // Minutos: 0, 15, 30, 45 (usamos índices 0..3 e mapeamos)
        final int[] minuteValues = new int[]{0, 15, 30, 45};
        String[] minuteDisplayed = new String[]{"00", "15", "30", "45"};
        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(minuteValues.length - 1);
        binding.minutePicker.setDisplayedValues(minuteDisplayed);
        binding.minutePicker.setWrapSelectorWheel(false);

        // Aplicar valores iniciais do ViewModel
        Integer horas = viewModel.getDuracaoTreinoHoras().getValue();
        Integer mins = viewModel.getDuracaoTreinoMinutosParte().getValue();
        if (horas != null) binding.hourPicker.setValue(horas);
        if (mins != null) {
            int idx = 0;
            for (int i = 0; i < minuteValues.length; i++) {
                if (minuteValues[i] == mins) { idx = i; break; }
            }
            binding.minutePicker.setValue(idx);
        }
        binding.minutePicker.setEnabled(horas == null || horas < 24);

        binding.hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            // Se 24h, força minutos em 0
            if (newVal >= 24) {
                binding.minutePicker.setValue(0);
                binding.minutePicker.setEnabled(false);
                viewModel.updateDuracaoTreino(24, 0);
            } else {
                binding.minutePicker.setEnabled(true);
                int minuteIdx = binding.minutePicker.getValue();
                viewModel.updateDuracaoTreino(newVal, minuteValues[minuteIdx]);
            }
        });

        binding.minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int horasAtual = binding.hourPicker.getValue();
            // Se horas 24, ignore alterações (já desabilitado, mas por segurança)
            if (horasAtual >= 24) {
                binding.minutePicker.setValue(0);
                return;
            }
            viewModel.updateDuracaoTreino(horasAtual, minuteValues[newVal]);
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
                Toast.makeText(this, "Atividade adicionada com sucesso! 🎉", Toast.LENGTH_SHORT).show();
                // Limpar seleções após sucesso para permitir novo registro
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

        // Resetar pickers para 1h00
        binding.hourPicker.setValue(1);
        binding.minutePicker.setValue(0);
        binding.minutePicker.setEnabled(true);

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
        String horasText;
        if (horas == 1.0f) {
            horasText = "1h";
        } else {
            horasText = String.format("%.1fh", horas);
        }
        horasTextView.setText(horasText);
        horasTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        horasTextView.setTextSize(16);
        horasTextView.setGravity(android.view.Gravity.END);

        // Adicionar TextViews à linha
        row.addView(tipoTextView);
        row.addView(horasTextView);

        // Adicionar linha à tabela
        tableLayout.addView(row);

        // Adicionar separador visual (linha divisória) apenas se não for a última linha
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                1
        ));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        tableLayout.addView(divider);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }
}