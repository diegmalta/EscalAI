package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.enums.HumorValues;
import com.ufrj.escalaiv2.enums.Event;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.AtividadesRepository;
import com.ufrj.escalaiv2.repository.UsuarioRepository;
import com.ufrj.escalaiv2.repository.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HumorVM extends AndroidViewModel {
    private final AtividadesRepository atividadesRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExecutorService executorService;

    // MutableLiveData for each mood metric
    private final MutableLiveData<Integer> joyLevel;
    private final MutableLiveData<Integer> sadnessLevel;
    private final MutableLiveData<Integer> anxietyLevel;
    private final MutableLiveData<Integer> stressLevel;
    private final MutableLiveData<Integer> calmLevel;

    // MutableLiveData for mood text values
    private final MutableLiveData<String> joyText;
    private final MutableLiveData<String> sadnessText;
    private final MutableLiveData<String> anxietyText;
    private final MutableLiveData<String> stressText;
    private final MutableLiveData<String> calmText;

    private final MutableLiveData<Event> uiEvent;

    public HumorVM(Application application) {
        super(application);
        atividadesRepository = new AtividadesRepository(application);
        usuarioRepository = new UsuarioRepository(application);
        executorService = Executors.newSingleThreadExecutor();

        // Initialize mood levels with default values (2 = MODERATE)
        joyLevel = new MutableLiveData<>(HumorValues.MODERATE.ordinal());
        sadnessLevel = new MutableLiveData<>(HumorValues.MODERATE.ordinal());
        anxietyLevel = new MutableLiveData<>(HumorValues.MODERATE.ordinal());
        stressLevel = new MutableLiveData<>(HumorValues.MODERATE.ordinal());
        calmLevel = new MutableLiveData<>(HumorValues.MODERATE.ordinal());

        // Initialize mood text values
        joyText = new MutableLiveData<>(getMoodTextForValue(HumorValues.MODERATE));
        sadnessText = new MutableLiveData<>(getMoodTextForValue(HumorValues.MODERATE));
        anxietyText = new MutableLiveData<>(getMoodTextForValue(HumorValues.MODERATE));
        stressText = new MutableLiveData<>(getMoodTextForValue(HumorValues.MODERATE));
        calmText = new MutableLiveData<>(getMoodTextForValue(HumorValues.MODERATE));

        uiEvent = new MutableLiveData<>();

        // Initialize mood values if they exist
        initializeMoodValues();
    }

    private void initializeMoodValues() {
        AuthRepository authRepository = new AuthRepository(getApplication());
        authRepository.getCurrentUserIdAsync(currentUserId -> {
            executorService.execute(() -> {
                // Carregar valores de humor anteriores se existirem para hoje
                UserDailyData todayMood = atividadesRepository.getTodayMoodData(currentUserId);

                if (todayMood != null) {
                    joyLevel.postValue(todayMood.getJoyLevel());
                    sadnessLevel.postValue(todayMood.getSadnessLevel());
                    anxietyLevel.postValue(todayMood.getAnxietyLevel());
                    stressLevel.postValue(todayMood.getStressLevel());
                    calmLevel.postValue(todayMood.getCalmLevel());

                    // Atualizar todos os textos de humor na UI thread
                    updateAllMoodTexts();
                }
            });
        });
    }

    private void updateAllMoodTexts() {
        joyText.postValue(getMoodTextForLevel(joyLevel.getValue()));
        sadnessText.postValue(getMoodTextForLevel(sadnessLevel.getValue()));
        anxietyText.postValue(getMoodTextForLevel(anxietyLevel.getValue()));
        stressText.postValue(getMoodTextForLevel(stressLevel.getValue()));
        calmText.postValue(getMoodTextForLevel(calmLevel.getValue()));
    }

    // Getters for mood levels
    public LiveData<Integer> getJoyLevel() {
        return joyLevel;
    }

    public LiveData<Integer> getSadnessLevel() {
        return sadnessLevel;
    }

    public LiveData<Integer> getAnxietyLevel() {
        return anxietyLevel;
    }

    public LiveData<Integer> getStressLevel() {
        return stressLevel;
    }

    public LiveData<Integer> getCalmLevel() {
        return calmLevel;
    }

    // Getters for mood texts
    public LiveData<String> getJoyText() {
        return joyText;
    }

    public LiveData<String> getSadnessText() {
        return sadnessText;
    }

    public LiveData<String> getAnxietyText() {
        return anxietyText;
    }

    public LiveData<String> getStressText() {
        return stressText;
    }

    public LiveData<String> getCalmText() {
        return calmText;
    }

    public LiveData<Event> getUiEvent() {
        return uiEvent;
    }

    // Methods to update mood levels
    public void updateJoyLevel(int level) {
        joyLevel.setValue(level);
        joyText.setValue(getMoodTextForLevel(level));
    }

    public void updateSadnessLevel(int level) {
        sadnessLevel.setValue(level);
        sadnessText.setValue(getMoodTextForLevel(level));
    }

    public void updateAnxietyLevel(int level) {
        anxietyLevel.setValue(level);
        anxietyText.setValue(getMoodTextForLevel(level));
    }

    public void updateStressLevel(int level) {
        stressLevel.setValue(level);
        stressText.setValue(getMoodTextForLevel(level));
    }

    public void updateCalmLevel(int level) {
        calmLevel.setValue(level);
        calmText.setValue(getMoodTextForLevel(level));
    }

    // Helper method to convert level to text description
    private String getMoodTextForLevel(int level) {
        return getMoodTextForValue(HumorValues.values()[level]);
    }

    // Helper method to convert HumorValues to text description
    private String getMoodTextForValue(HumorValues value) {
        switch (value) {
            case NONE:
                return "Nada";
            case LOW:
                return "Pouco";
            case MODERATE:
                return "Normal";
            case HIGH:
                return "Muito";
            default:
                return "Normal";
        }
    }

    // Save all mood data
    public void saveMoodData() {
        Integer joy = joyLevel.getValue();
        Integer sadness = sadnessLevel.getValue();
        Integer anxiety = anxietyLevel.getValue();
        Integer stress = stressLevel.getValue();
        Integer calm = calmLevel.getValue();

        if (joy != null && sadness != null && anxiety != null && stress != null && calm != null) {
            AuthRepository authRepository = new AuthRepository(getApplication());
            authRepository.getCurrentUserIdAsync(currentUserId -> {
                String token = authRepository.getAuthTokenRaw();
                if (token == null) {
                    token = authRepository.getAuthToken();
                }
                if (token != null && !token.startsWith("Bearer ")) {
                    token = "Bearer " + token;
                }

                // Registrar humor usando o novo repositório
                atividadesRepository.registrarHumor(
                        currentUserId,
                        HumorValues.values()[joy],
                        HumorValues.values()[sadness],
                        HumorValues.values()[anxiety],
                        HumorValues.values()[stress],
                        HumorValues.values()[calm],
                        token,
                        new AtividadesRepository.OnActivityCallback() {
                            @Override
                            public void onSuccess() {
                                uiEvent.postValue(Event.SHOW_SUCCESS_MESSAGE);
                            }

                            @Override
                            public void onError(String message) {
                                uiEvent.postValue(Event.SHOW_ERROR_MESSAGE);
                            }
                        }
                );
            });
        } else {
            uiEvent.setValue(Event.SHOW_ERROR_MESSAGE);
        }
    }


}