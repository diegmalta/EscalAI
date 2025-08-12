package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.repository.AtividadesRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;
import android.content.Context;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.ufrj.escalaiv2.repository.AuthRepository;
import android.util.Log;

public class AguaVM extends AndroidViewModel {
    private final AtividadesRepository atividadesRepository;
    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Integer> totalWaterConsumption;
    private final MutableLiveData<Integer> valorAtualSlider;
    private final MutableLiveData<Event> uiEvent;
    private final MutableLiveData<String> recommendedWater;

    public AguaVM(Application application) {
        super(application);
        atividadesRepository = new AtividadesRepository(application);
        usuarioRepository = new UsuarioRepository(application);

        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();
        recommendedWater = new MutableLiveData<>("2.0 L");
        totalWaterConsumption = new MutableLiveData<>(0); // Inicializa com 0 ou null

        // Buscar valor do banco de dados em background
        atividadesRepository.getCurrentUserId(currentUserId -> {
            new Thread(() -> {
                int total = atividadesRepository.getTotalWaterConsumption(currentUserId);
                ((MutableLiveData<Integer>) totalWaterConsumption).postValue(total);
            }).start();
        });
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
            atividadesRepository.getCurrentUserId(currentUserId -> {
                // Buscar token salvo usando AuthRepository
                AuthRepository authRepository = new AuthRepository(getApplication());

                // Verificar se o token existe
                boolean hasToken = authRepository.hasToken();
                Log.d("TOKEN_DEBUG", "hasToken(): " + hasToken);

                // Tentar obter o token sem verificar autenticação
                String token = authRepository.getAuthTokenRaw();
                Log.d("TOKEN_DEBUG", "Token recuperado (raw): " + token);

                // Se não encontrou, tentar com verificação de autenticação
                if (token == null) {
                    token = authRepository.getAuthToken();
                    Log.d("TOKEN_DEBUG", "Token recuperado (com auth): " + token);
                }

                if (token != null && !token.startsWith("Bearer ")) {
                    token = "Bearer " + token;
                }
                Log.d("TOKEN_DEBUG", "Token enviado: " + token);

                // Registrar água usando o novo repositório
                atividadesRepository.registrarAgua(currentUserId, sliderValue, token, new AtividadesRepository.OnActivityCallback() {
                    @Override
                    public void onSuccess() {
                        valorAtualSlider.postValue(0);
                        new Thread(() -> {
                            int total = atividadesRepository.getTotalWaterConsumption(currentUserId);
                            ((MutableLiveData<Integer>) totalWaterConsumption).postValue(total);
                        }).start();
                        uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("TOKEN_DEBUG", "Erro na requisição: " + message);
                        uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                    }
                });
            });
        } else {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
        }
    }

    // Resetar consumo total de água
    public void resetarConsumoAgua() {
        atividadesRepository.getCurrentUserId(currentUserId -> {
            atividadesRepository.resetWaterConsumption(currentUserId);

            // Resetar outros valores
            valorAtualSlider.setValue(0);
            uiEvent.setValue(Event.RESET_COMPLETED);
            // Buscar valor do banco de dados em background
            new Thread(() -> {
                int total = atividadesRepository.getTotalWaterConsumption(currentUserId);
                ((MutableLiveData<Integer>) totalWaterConsumption).postValue(total);
            }).start();
        });
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