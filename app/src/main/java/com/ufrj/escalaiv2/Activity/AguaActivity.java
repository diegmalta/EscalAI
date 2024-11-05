package com.ufrj.escalaiv2.Activity;

import com.ufrj.escalaiv2.ViewModel.AguaVM

public class AguaActivity extends AppCompatActivity {
    private ActivityAguaBinding binding;
    private AguaVM aguaVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAguaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        aguaVM = new ViewModelProvider(this).get(AguaVM.class);

        setupUI();
        observeViewModel();
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
        // Observar o valor total de água
        aguaVM.getTotalWaterAmount().observe(this, amount ->
                binding.waterAmount.setText(String.format("%d ml", amount)));

        // Observar o valor atual do slider
        aguaVM.getValorAtualSlider().observe(this, value -> {
            binding.sliderValue.setText(String.format("%d ml", value));
            binding.waterSlider.setValue(value);
        });

        // Observar eventos da UI
        aguaVM.getUiEvent().observe(this, event -> {
            if (event == AguaEvent.SHOW_SUCCESS_MESSAGE) {
                Snackbar.make(
                        binding.getRoot(),
                        "Consumo de água registrado com sucesso!",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });
    }
}
