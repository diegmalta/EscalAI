package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.repository.AtividadesRepository;
import com.ufrj.escalaiv2.repository.AuthRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

public class SonoVM extends AndroidViewModel {
    private final AtividadesRepository atividadesRepository;
    private final UsuarioRepository usuarioRepository;

    // Horários de sono
    private final MutableLiveData<String> sleepTime;
    private final MutableLiveData<String> wakeTime;

    // Duração total de sono calculada
    private final MutableLiveData<String> totalSleepTime;
    private final MutableLiveData<Integer> totalSleepTimeInMinutes;

    // Qualidade do sono (1 a 5)
    private final MutableLiveData<Integer> sleepQuality;

    // Qualidade do sono em texto
    private final MutableLiveData<String> sleepQualityText;

    // Recomendação de sono
    private final MutableLiveData<String> recommendedSleep;

    // Eventos de UI
    private final MutableLiveData<Event> uiEvent;

    // Array com os textos de qualidade de sono
    private final String[] qualityLabels = {"Péssima", "Ruim", "OK", "Boa", "Excelente"};

    public SonoVM(Application application) {
        super(application);
        atividadesRepository = new AtividadesRepository(application);
        usuarioRepository = new UsuarioRepository(application);

        // Inicializar valores
        sleepTime = new MutableLiveData<>("00:00");
        wakeTime = new MutableLiveData<>("08:00");
        totalSleepTime = new MutableLiveData<>("0 horas e 00 minutos)");
        totalSleepTimeInMinutes = new MutableLiveData<>(0);
        sleepQuality = new MutableLiveData<>(3); // Valor padrão: OK
        sleepQualityText = new MutableLiveData<>("Qualidade: OK");
        recommendedSleep = new MutableLiveData<>("8 horas");
        uiEvent = new MutableLiveData<>();

        // Calcular duração inicial
        calculateSleepDuration();
    }

    // Getters para LiveData
    public LiveData<String> getSleepTime() {
        return sleepTime;
    }

    public LiveData<String> getWakeTime() {
        return wakeTime;
    }

    public LiveData<String> getTotalSleepTime() {
        return totalSleepTime;
    }

    public LiveData<Integer> getTotalSleepTimeInMinutes() {
        return totalSleepTimeInMinutes;
    }

    public LiveData<Integer> getSleepQuality() {
        return sleepQuality;
    }

    public LiveData<String> getSleepQualityText() {
        return sleepQualityText;
    }

    public LiveData<String> getRecommendedSleep() {
        return recommendedSleep;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    // Método para definir o horário que dormiu
    public void setSleepTime(int hour, int minute) {
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        sleepTime.setValue(formattedTime);
        calculateSleepDuration();
    }

    // Método para definir o horário que acordou
    public void setWakeTime(int hour, int minute) {
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        wakeTime.setValue(formattedTime);
        calculateSleepDuration();
    }

    // Método para definir a qualidade do sono
    public void setSleepQuality(int quality) {
        if (quality >= 1 && quality <= 5) {
            sleepQuality.setValue(quality);
            updateSleepQualityText(quality);
        }
    }

    // Atualizar texto da qualidade do sono
    private void updateSleepQualityText(int quality) {
        String qualityText = "Qualidade: " + qualityLabels[quality - 1];
        sleepQualityText.setValue(qualityText);
    }

    // Calcular duração do sono com base nos horários
    private void calculateSleepDuration() {
        String sleepTimeStr = sleepTime.getValue();
        String wakeTimeStr = wakeTime.getValue();

        if (sleepTimeStr == null || wakeTimeStr == null) {
            totalSleepTime.setValue("0h 00min");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            // Parse dos horários
            Date sleepDate = format.parse(sleepTimeStr);
            Date wakeDate = format.parse(wakeTimeStr);

            if (sleepDate != null && wakeDate != null) {
                // Configurar calendários
                Calendar sleepCalendar = Calendar.getInstance();
                sleepCalendar.setTime(sleepDate);

                Calendar wakeCalendar = Calendar.getInstance();
                wakeCalendar.setTime(wakeDate);

                // Se o horário de acordar for antes do horário de dormir, significa que a pessoa dormiu um dia e acordou no outro
                if (wakeCalendar.before(sleepCalendar)) {
                    wakeCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                // Calcular diferença em minutos
                long diffMillis = wakeCalendar.getTimeInMillis() - sleepCalendar.getTimeInMillis();
                int totalMinutes = (int) (diffMillis / (1000 * 60));
                totalSleepTimeInMinutes.setValue(totalMinutes);
                // Converter para horas e minutos
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;

                totalSleepTime.setValue(String.format(Locale.getDefault(), "%dh %02dmin", hours, minutes));
            }
        } catch (ParseException e) {
            totalSleepTime.setValue("0h 00min");
        }
    }

    // Salvar registro de sono
    public void salvarRegistroSono() {
        Integer quality = sleepQuality.getValue();

        if (quality != null) {
            atividadesRepository.getCurrentUserId(currentUserId -> {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                AuthRepository authRepository = new AuthRepository(getApplication());
                String token = authRepository.getAuthTokenRaw();

                if (token == null) {
                    token = authRepository.getAuthToken();
                }

                if (token != null && !token.startsWith("Bearer ")) {
                    token = "Bearer " + token;
                }

                // Registrar sono usando o novo repositório
                atividadesRepository.registrarSono(currentDate, sleepTime.getValue(),
                        wakeTime.getValue(), totalSleepTimeInMinutes.getValue(), quality, token,
                        new AtividadesRepository.OnActivityCallback() {
                            @Override
                            public void onSuccess() {
                                uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                            }

                            @Override
                            public void onError(String message) {
                                uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                            }
                        });
            });
        } else {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
        }
    }

    // Resetar dados
    public void reset() {
        sleepTime.setValue("00:00");
        wakeTime.setValue("08:00");
        sleepQuality.setValue(3);
        updateSleepQualityText(3);
        calculateSleepDuration();
        uiEvent.setValue(Event.RESET_COMPLETED);
    }


}