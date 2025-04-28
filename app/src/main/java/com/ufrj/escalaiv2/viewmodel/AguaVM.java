package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.repository.UserDailyDataRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

public class AguaVM extends AndroidViewModel {
    private final UserDailyDataRepository userDailyDataRepository;
    private final UsuarioRepository usuarioRepository;
    private final LiveData<Integer> totalWaterConsumption;
    private final MutableLiveData<Integer> valorAtualSlider;
    private final MutableLiveData<Event> uiEvent;
    private final MutableLiveData<String> recommendedWater;

    public AguaVM(Application application) {
        super(application);
        userDailyDataRepository = new UserDailyDataRepository(application);
        usuarioRepository = new UsuarioRepository(application);

        int currentUserId = getCurrentUserId();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        totalWaterConsumption = userDailyDataRepository.getTotalWaterConsumptionLiveData(currentUserId, today);

        // Criar MutableLiveData para valores que você precisa modificar
        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();
        recommendedWater = new MutableLiveData<>("2.0 L"); // Recomendação padrão
    }

    public LiveData<Integer> getTotalWaterConsumption() {
        return totalWaterConsumption;
    }

    public LiveData<Integer> getValorAtualSlider() {
        return valorAtualSlider;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    public LiveData<String> getTotalWaterRecommended() {
        return recommendedWater;
    }

    // Atualizar valor do slider
    public void atualizarValorSlider(int value) {
        if (value >= 0 && value <= 500) {  // Validando contra os limites do slider
            valorAtualSlider.setValue(value);
        }
    }

    // Confirmar consumo de água
    public void confirmarConsumoAgua() {
        Integer sliderValue = valorAtualSlider.getValue();

        if (sliderValue != null && sliderValue > 0) {
            int currentUserId = getCurrentUserId();

            // Adicionar consumo de água ao repositório
            userDailyDataRepository.addWaterConsumption(currentUserId, sliderValue);

            // Resetar slider
            valorAtualSlider.setValue(0);

            // Acionar evento de UI
            uiEvent.setValue(Event.SHOW_SUCCESS_MESSAGE);
        } else {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
        }
    }

    // Resetar consumo total de água
    public void resetarConsumoAgua() {
        int currentUserId = getCurrentUserId();
        userDailyDataRepository.resetWaterConsumption(currentUserId);

        // Resetar outros valores
        valorAtualSlider.setValue(0);
        uiEvent.setValue(Event.RESET_COMPLETED);
    }

    // Método para obter o ID do usuário atual
    private int getCurrentUserId() {
        // Pegar o ID do usuário logado atual, usando SharedPreferences
        return 1; // Substituir pela implementação real
    }

    // Formatar quantidade de água para exibição
    public String formatarQuantidadeAgua(int quantidadeML) {
        if (quantidadeML >= 1000) {
            float quantidadeL = quantidadeML / 1000f;
            return String.format("%.1f L", quantidadeL);
        }
        return quantidadeML + " ml";
    }

    // Atualizar recomendação de água
    public void atualizarRecomendacaoAgua(float litros) {
        recommendedWater.setValue(String.format("%.1f L", litros));
    }
}