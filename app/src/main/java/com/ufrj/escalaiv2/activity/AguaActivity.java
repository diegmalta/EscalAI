package com.ufrj.escalaiv2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import com.ufrj.escalaiv2.databinding.ActivityAguaBinding;
import com.google.android.material.snackbar.Snackbar;
import androidx.databinding.DataBindingUtil;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.viewmodel.AguaVM;

public class AguaActivity extends AppCompatActivity {
    private ActivityAguaBinding binding;
    private AguaVM aguaVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agua);

        // Opcional: setar o ViewModel no binding se estiver usando data binding
        aguaVM = new ViewModelProvider(this).get(AguaVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(aguaVM);

        setContentView(binding.getRoot());
        setupUI();
    }

    private void setupUI() {
        // Configurar a toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Configurar o slider
        binding.waterSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                aguaVM.atualizarValorSlider((int) value);
            }
        });

        // Configurar o botão de confirmar
        binding.confirmButton.setOnClickListener(v -> aguaVM.confirmarConsumoAgua());
    }

    private void observeViewModel() {
        aguaVM.getTotalWaterConsumption().observe(this, amount ->
                binding.waterAmount.setText(String.format("%d ml", amount)));

        aguaVM.getValorAtualSlider().observe(this, value -> {
            binding.sliderValue.setText(String.format("%d ml", value));
            binding.waterSlider.setValue(value);
        });

        aguaVM.getUiEvent().observe(this, event -> {
            Snackbar.make(
                    binding.getRoot(),
                    "Consumo de água registrado!",
                    Snackbar.LENGTH_SHORT
            ).show();
        });
    }
}
