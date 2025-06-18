package com.ufrj.escalaiv2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.ufrj.escalaiv2.databinding.ActivityAguaBinding;
import com.google.android.material.snackbar.Snackbar;
import androidx.databinding.DataBindingUtil;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.viewmodel.AguaVM;
import com.ufrj.escalaiv2.viewmodel.AguaVMFactory;

public class AguaActivity extends AppCompatActivity {
    private ActivityAguaBinding binding;
    private AguaVM aguaVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agua);

        AguaVMFactory factory = new AguaVMFactory(getApplication());
        aguaVM = new ViewModelProvider(this, factory).get(AguaVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(aguaVM);

        //TODO testar comentando o setContentView
        setContentView(binding.getRoot());
        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        // Configurar a toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher());

        // Configurar o slider
        binding.waterSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                aguaVM.atualizarValorSlider((int) value);
            }
        });

        // Configurar o botão de confirmar
        binding.confirmButton.setOnClickListener(v -> aguaVM.confirmarConsumoAgua());
        
        // Configurar o botão de zerar
        binding.resetButton.setOnClickListener(v -> aguaVM.resetarConsumoAgua());
    }

    private void observeViewModel() {
        aguaVM.getTotalWaterConsumption().observe(this, amount ->
                binding.waterAmount.setText(String.format("%d ml", amount)));

        aguaVM.getValorAtualSlider().observe(this, value -> {
            binding.sliderValue.setText(String.format("%d ml", value));
            binding.waterSlider.setValue(value);
        });

        aguaVM.getUiEvent().observe(this, event -> {
            if (event == Event.SHOW_SUCCESS_MESSAGE) {
                Snackbar.make(
                        binding.getRoot(),
                        "Consumo de água registrado!",
                        BaseTransientBottomBar.LENGTH_SHORT
                ).show();
            } else if (event == Event.RESET_COMPLETED) {
                Snackbar.make(
                        binding.getRoot(),
                        "Consumo de água do dia zerado!",
                        BaseTransientBottomBar.LENGTH_SHORT
                ).show();
            }
        });
    }
}
