package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivityDorBinding;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.viewmodel.DorVM;
import com.ufrj.escalaiv2.viewmodel.DorVMFactory;

public class DorActivity extends AppCompatActivity {

    private DorVM dorVM;
    private ActivityDorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dor);

        // Inicializar o ViewModel
        DorVMFactory factory = new DorVMFactory(getApplication());
        dorVM = new ViewModelProvider(this, factory).get(DorVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(dorVM);

        // Resetar todos os campos ao abrir a tela (sempre iniciar vazio)
        dorVM.resetAllFields();

        // Configurar a interface
        setupToolbar();
        setupDropdowns();
        setupSlider();
        setupSaveButton();
        observeUiEvents();
        observeSelectionChanges();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDropdowns() {
        // Configurar o dropdown de áreas
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dorVM.getAreas()
        );
        binding.areaDropdown.setAdapter(areaAdapter);
        binding.areaDropdown.setOnClickListener(v -> binding.areaDropdown.showDropDown());

        // Listener para o dropdown de áreas
        binding.areaDropdown.setOnItemClickListener((parent, view, position, id) -> {
            dorVM.updateSelectedArea(position);

            // Atualizar o adapter da subárea
            ArrayAdapter<String> subareaAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    dorVM.getSubareas(position)
            );
            binding.subareaDropdown.setAdapter(subareaAdapter);
            binding.subareaDropdown.setText("", false);
        });

        // Listener para o dropdown de subáreas (configurado dinamicamente quando uma área é selecionada)
        binding.subareaDropdown.setOnItemClickListener((parent, view, position, id) -> {
            dorVM.updateSelectedSubarea(position);

            // Atualizar o adapter da especificação
            Integer subareaId = dorVM.getSelectedSubarea().getValue();
            ArrayAdapter<String> especificacaoAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    dorVM.getEspecificacoesBySubareaId(subareaId)
            );
            binding.especificacaoDropdown.setAdapter(especificacaoAdapter);
            binding.especificacaoDropdown.setText("", false);
        });

        // Listener para o dropdown de especificações (configurado dinamicamente quando uma subárea é selecionada)
        binding.especificacaoDropdown.setOnItemClickListener((parent, view, position, id) -> {
            dorVM.updateSelectedEspecificacao(position);
        });

        // Garantir que os dropdowns sempre mostrem todas as opções ao ganhar foco
        binding.subareaDropdown.setOnClickListener(v -> binding.subareaDropdown.showDropDown());
        binding.especificacaoDropdown.setOnClickListener(v -> binding.especificacaoDropdown.showDropDown());
    }

    private void setupSlider() {
        binding.intensidadeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                dorVM.updateIntensidadeDor((int) value);
            }
        });
    }

    private void setupSaveButton() {
        binding.saveButton.setOnClickListener(v -> dorVM.saveDorData());
    }

    private void observeUiEvents() {
        dorVM.getUiEvent().observe(this, event -> {
            if (event == Event.SHOW_SUCCESS_MESSAGE) {
                Toast.makeText(this, "Dor registrada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else if (event == Event.SHOW_ERROR_MESSAGE) {
                Toast.makeText(this, "Erro ao registrar a dor. Verifique se todos os campos estão preenchidos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeSelectionChanges() {
        // Observar mudanças na área selecionada para atualizar o dropdown de subáreas
        dorVM.getSelectedArea().observe(this, areaPosition -> {
            if (areaPosition >= 0) {
                ArrayAdapter<String> subareaAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        dorVM.getSubareas(areaPosition)
                );
                binding.subareaDropdown.setAdapter(subareaAdapter);

                // Limpar texto atual se não tiver sido definido pelo ViewModel
                binding.subareaDropdown.setText("", false);
            }
        });

        // Observar mudanças na subárea selecionada para atualizar o dropdown de especificações
        dorVM.getSelectedSubarea().observe(this, subareaId -> {
            if (subareaId != null && subareaId >= 0) {
                ArrayAdapter<String> especificacaoAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        dorVM.getEspecificacoesBySubareaId(subareaId)
                );
                binding.especificacaoDropdown.setAdapter(especificacaoAdapter);

                // Limpar texto atual se não tiver sido definido pelo ViewModel
                binding.especificacaoDropdown.setText("", false);
            }
        });
    }
}