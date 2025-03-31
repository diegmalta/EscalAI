package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.event.AguaEvent;
import com.ufrj.escalaiv2.repository.HumorRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

public class HumorVM extends AndroidViewModel {
    private final HumorRepository humorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Integer> anxiousLevel;
    private final MutableLiveData<Integer> happinessLevel;
    private final MutableLiveData<Integer> sadnessLevel;
    private final MutableLiveData<Integer> stressLevel;
    private final MutableLiveData<Integer> calmLevel;
    private final MutableLiveData<AguaEvent> uiEvent;

    public HumorVM(Application application) {
        super(application);
        humorRepository = new HumorRepository(application);
        usuarioRepository = new UsuarioRepository(application);

        totalWaterConsumption = new MutableLiveData<>(0);
        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();

        // Initialize total water consumption (you'll need to pass the current user's ID)
        initializeTotalWaterConsumption();
    }

    private void initializeTotalWaterConsumption() {
        // This is a placeholder. You'll need to implement a way to get the current user's ID
        int currentUserId = getCurrentUserId(); // You'll need to implement this method
        int totalConsumption = humorRepository.getTotalWaterConsumption(currentUserId);
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

    public void atualizarValorSlider(int value) {
        if (value >= 0 && value <= 500) {  // Validating against slider bounds
            valorAtualSlider.setValue(value);
        }
    }

    // Method to get current user ID (you'll need to implement this)
    private int getCurrentUserId() {
        // pegar o id do usuario logado atual, usando sharedpreferences
        return 1;
    }
}