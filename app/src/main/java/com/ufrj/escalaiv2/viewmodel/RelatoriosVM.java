package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.UserDailyDataRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RelatoriosVM extends AndroidViewModel {

    private final UserDailyDataRepository repository;
    private final LiveData<List<UserDailyData>> last7DaysData;
    private final int currentUserId = 1;

    public RelatoriosVM(Application application) {
        super(application);
        repository = new UserDailyDataRepository(application);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = sdf.format(cal.getTime());

        last7DaysData = repository.getDailyDataBetweenDatesLiveData(currentUserId, startDate, endDate);
    }

    public LiveData<List<UserDailyData>> getLast7DaysData() {
        return last7DaysData;
    }

    // --- LiveData específicos para cada gráfico ---
    public LiveData<Map<String, Integer>> getWaterConsumptionLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Integer> waterDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    waterDataMap.put(data.getDate(), data.getWaterConsumed());
                }
            }
            return waterDataMap;
        });
    }

    public LiveData<Map<String, Integer>> getSleepDurationLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Integer> sleepDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    sleepDataMap.put(data.getDate(), data.getTotalSleepTimeInMinutes());
                }
            }
            return sleepDataMap;
        });
    }

    public LiveData<Map<String, Integer>> getPainIntensityLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Integer> painDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    painDataMap.put(data.getDate(), data.getIntensidadeDor());
                }
            }
            return painDataMap;
        });
    }

    public LiveData<Map<String, Integer>> getTrainingDurationLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Integer> trainingDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    int dailyTotalMinutes = 0;
                    // Verifica se o mapa de treinos não é nulo
                    if (data.getTreinosMap() != null) {
                        // Soma a duração de todos os treinos registrados no mapa para aquele dia
                        for (Integer duration : data.getTreinosMap().values()) {
                            if (duration != null) {
                                dailyTotalMinutes += duration;
                            }
                        }
                    }
                    // Adiciona a duração total do dia ao mapa do gráfico
                    trainingDataMap.put(data.getDate(), dailyTotalMinutes);
                }
            }
            return trainingDataMap;
        });
    }

    public LiveData<Map<String, Map<String, Integer>>> getMoodLevelsLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Map<String, Integer>> moodDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    Map<String, Integer> dailyMoods = new TreeMap<>();
                    dailyMoods.put("Alegria", data.getJoyLevel());
                    dailyMoods.put("Tristeza", data.getSadnessLevel());
                    dailyMoods.put("Ansiedade", data.getAnxietyLevel());
                    dailyMoods.put("Estresse", data.getStressLevel());
                    dailyMoods.put("Calma", data.getCalmLevel());
                    moodDataMap.put(data.getDate(), dailyMoods);
                }
            }
            return moodDataMap;
        });
    }
}

