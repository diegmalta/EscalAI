package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivityHumorBinding;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.viewmodel.HumorVM;
import com.ufrj.escalaiv2.viewmodel.HumorVMFactory;

public class HumorActivity extends AppCompatActivity {

    private HumorVM humorVM;
    private ActivityHumorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_humor);

        HumorVMFactory factory = new HumorVMFactory(getApplication());
        humorVM = new ViewModelProvider(this, factory).get(HumorVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(humorVM);

        // Calling the methods that set up the UI and observers
        observeUiEvents();
        setupToolbar();
        setupSliders();
        setupSaveButton();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSliders() {
        // Joy slider
        binding.joySlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                humorVM.updateJoyLevel((int) value);
            }
        });

        // Sadness slider
        binding.sadnessSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                humorVM.updateSadnessLevel((int) value);
            }
        });

        // Anxiety slider
        binding.anxietySlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                humorVM.updateAnxietyLevel((int) value);
            }
        });

        // Stress slider
        binding.stressSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                humorVM.updateStressLevel((int) value);
            }
        });

        // Calm slider
        binding.calmSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                humorVM.updateCalmLevel((int) value);
            }
        });
    }

    private void setupSaveButton() {
        binding.saveButton.setOnClickListener(v -> humorVM.saveMoodData());
    }

    private void observeUiEvents() {
        humorVM.getUiEvent().observe(this, event -> {
            if (event == Event.SHOW_SUCCESS_MESSAGE) {
                Toast.makeText(this, "Humor salvo com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else if (event == Event.SHOW_ERROR_MESSAGE) {
                Toast.makeText(this, "Erro ao salvar o humor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}