package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.event.AguaEvent;
import com.ufrj.escalaiv2.repository.HumorRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

public class AguaVM extends AndroidViewModel {
    private final HumorRepository aguaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Integer> totalWaterConsumption;
    private final MutableLiveData<Integer> valorAtualSlider;
    private final MutableLiveData<AguaEvent> uiEvent;
    private final MutableLiveData<String> recommendedWater;

    public AguaVM(Application application) {
        super(application);
        aguaRepository = new HumorRepository(application);
        usuarioRepository = new UsuarioRepository(application);

        totalWaterConsumption = new MutableLiveData<>(0);
        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();
        recommendedWater = new MutableLiveData<>("2.0 L"); // Default recommendation

        // Initialize total water consumption (you'll need to pass the current user's ID)
        initializeTotalWaterConsumption();
    }

    private void initializeTotalWaterConsumption() {
        // This is a placeholder. You'll need to implement a way to get the current user's ID
        int currentUserId = getCurrentUserId(); // You'll need to implement this method
        int totalConsumption = aguaRepository.getTotalWaterConsumption(currentUserId);
        totalWaterConsumption.setValue(totalConsumption);
    }

    public LiveData<Integer> getTotalWaterConsumption() {
        return getTotalWaterConsumption(getCurrentUserId());
    }
    public LiveData<Integer> getTotalWaterConsumption(int currentUserId) {
        return totalWaterConsumption;
    }

    public LiveData<Integer> getValorAtualSlider() {
        return valorAtualSlider;
    }

    public LiveData<AguaEvent> getUiEvent() {
        return uiEvent;
    }

    public LiveData<String> getTotalWaterRecommended() {
        return recommendedWater;
    }

    // Update slider value
    public void atualizarValorSlider(int value) {
        if (value >= 0 && value <= 500) {  // Validating against slider bounds
            valorAtualSlider.setValue(value);
        }
    }

    // Confirm water consumption
    public void confirmarConsumoAgua() {
        Integer sliderValue = valorAtualSlider.getValue();

        if (sliderValue != null && sliderValue > 0) {
            // Get current user ID (you'll need to implement this method)
            int currentUserId = getCurrentUserId();

            // Add water consumption to repository
            aguaRepository.addWaterConsumption(currentUserId, sliderValue);

            // Update total water consumption
            int totalConsumption = aguaRepository.getTotalWaterConsumption(currentUserId);
            totalWaterConsumption.setValue(totalConsumption);

            // Reset slider
            valorAtualSlider.setValue(0);

            // Trigger UI event
            uiEvent.setValue(AguaEvent.SHOW_SUCCESS_MESSAGE);
        } else {
            uiEvent.setValue(AguaEvent.SHOW_ERROR_MESSAGE);
        }
    }

    // Reset total water consumption
    public void resetarConsumoAgua() {
        // This method would need to be updated to work with the new repository
        totalWaterConsumption.setValue(0);
        valorAtualSlider.setValue(0);
        uiEvent.setValue(AguaEvent.RESET_COMPLETED);
    }

    // Method to get current user ID (you'll need to implement this)
    private int getCurrentUserId() {
        // pegar o id do usuario logado atual, usando sharedpreferences
        return 1;
    }

    // Format water amount for display
    public String formatarQuantidadeAgua(int quantidadeML) {
        if (quantidadeML >= 1000) {
            float quantidadeL = quantidadeML / 1000f;
            return String.format("%.1f L", quantidadeL);
        }
        return quantidadeML + " ml";
    }

    // Update water recommendation
    public void atualizarRecomendacaoAgua(float litros) {
        recommendedWater.setValue(String.format("%.1f L", litros));
    }
}