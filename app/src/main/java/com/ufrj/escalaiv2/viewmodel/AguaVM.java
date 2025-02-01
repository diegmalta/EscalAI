package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AguaVM extends AndroidViewModel {
    private final MutableLiveData<Integer> quantidadeTotalAgua;
    private final MutableLiveData<Integer> valorAtualSlider;
    private final MutableLiveData<WaterConsumptionEvent> uiEvent;
    private final MutableLiveData<String> recomendacaoAgua;

    // Enum for UI events
    public enum WaterConsumptionEvent {
        SHOW_SUCCESS_MESSAGE,
        SHOW_ERROR_MESSAGE,
        RESET_COMPLETED
    }

    public AguaVM(Application application) {
        super(application);
        quantidadeTotalAgua = new MutableLiveData<>(0);
        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();
        recomendacaoAgua = new MutableLiveData<>("2.0 L"); // Default recommendation
    }

    // Getters for LiveData
    public LiveData<Integer> getQuantidadeTotalAgua() {
        return quantidadeTotalAgua;
    }

    public LiveData<Integer> getValorAtualSlider() {
        return valorAtualSlider;
    }

    public LiveData<WaterConsumptionEvent> getUiEvent() {
        return uiEvent;
    }

    public LiveData<String> getRecomendacaoAgua() {
        return recomendacaoAgua;
    }

    // Update slider value
    public void atualizarValorSlider(int value) {
        if (value >= 0 && value <= 500) {  // Validating against slider bounds
            valorAtualSlider.setValue(value);
        }
    }

    // Confirm water consumption
    public void confirmarConsumoAgua() {
        Integer currentTotal = quantidadeTotalAgua.getValue();
        Integer sliderValue = valorAtualSlider.getValue();

        if (currentTotal != null && sliderValue != null) {
            int novoTotal = currentTotal + sliderValue;
            quantidadeTotalAgua.setValue(novoTotal);
            valorAtualSlider.setValue(0);  // Reset slider
            uiEvent.setValue(WaterConsumptionEvent.SHOW_SUCCESS_MESSAGE);
        } else {
            uiEvent.setValue(WaterConsumptionEvent.SHOW_ERROR_MESSAGE);
        }
    }

    // Reset total water consumption
    public void resetarConsumoAgua() {
        quantidadeTotalAgua.setValue(0);
        valorAtualSlider.setValue(0);
        uiEvent.setValue(WaterConsumptionEvent.RESET_COMPLETED);
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
        recomendacaoAgua.setValue(String.format("%.1f L", litros));
    }
}