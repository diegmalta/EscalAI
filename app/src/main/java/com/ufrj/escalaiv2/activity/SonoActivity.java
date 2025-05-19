package com.ufrj.escalaiv2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivitySonoBinding;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.viewmodel.SonoVM;
import com.ufrj.escalaiv2.viewmodel.SonoVMFactory;

import java.util.Calendar;

public class SonoActivity extends AppCompatActivity {
    private ActivitySonoBinding binding;
    private SonoVM sonoVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySonoBinding.inflate(getLayoutInflater());

        // Configurar ViewModel usando Factory
        SonoVMFactory factory = new SonoVMFactory(getApplication());
        sonoVM = new ViewModelProvider(this, factory).get(SonoVM.class);

        // Configurar data binding
        binding.setViewModel(sonoVM);
        binding.setLifecycleOwner(this);

        setContentView(binding.getRoot());

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Configurar seletores de horário
        binding.sleepTimeInput.setOnClickListener(v -> showTimePickerDialog(true));
        binding.wakeTimeInput.setOnClickListener(v -> showTimePickerDialog(false));

        // Configurar slider de qualidade do sono
        binding.sleepQualitySlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    sonoVM.setSleepQuality((int) value);
                }
            }
        });

        // Configurar botões
        binding.saveButton.setOnClickListener(v -> sonoVM.salvarRegistroSono());
        binding.resetButton.setOnClickListener(v -> sonoVM.reset());
    }

    private void showTimePickerDialog(final boolean isSleepTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (isSleepTime) {
                            sonoVM.setSleepTime(hourOfDay, minute);
                        } else {
                            sonoVM.setWakeTime(hourOfDay, minute);
                        }
                    }
                },
                hour,
                minute,
                true // formato 24h
        );
        timePickerDialog.show();
    }

    private void observeViewModel() {
        // Observar mudanças no horário de dormir
        sonoVM.getSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String time) {
                binding.sleepTimeInput.setText(time);
            }
        });

        // Observar mudanças no horário de acordar
        sonoVM.getWakeTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String time) {
                binding.wakeTimeInput.setText(time);
            }
        });

        // Observar mudanças na duração do sono
        sonoVM.getTotalSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String duration) {
                binding.sleepDuration.setText(duration);
            }
        });

        // Observar mudanças na qualidade do sono
        sonoVM.getSleepQuality().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer quality) {
                binding.sleepQualitySlider.setValue(quality);
            }
        });

        // Observar mudanças no texto da qualidade do sono
        sonoVM.getSleepQualityText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String qualityText) {
                binding.sleepQualityValue.setText(qualityText);
            }
        });

        // Observar eventos de UI
        sonoVM.getUiEvent().observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                if (event == Event.SHOW_SUCCESS_MESSAGE) {
                    Snackbar.make(
                            binding.getRoot(),
                            "Registro de sono salvo com sucesso!",
                            BaseTransientBottomBar.LENGTH_SHORT
                    ).show();
                } else if (event == Event.SHOW_ERROR_MESSAGE) {
                    Snackbar.make(
                            binding.getRoot(),
                            "Erro ao salvar registro de sono",
                            BaseTransientBottomBar.LENGTH_SHORT
                    ).show();
                } else if (event == Event.RESET_COMPLETED) {
                    Snackbar.make(
                            binding.getRoot(),
                            "Dados de sono resetados",
                            BaseTransientBottomBar.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }
}