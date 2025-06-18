package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ufrj.escalaiv2.enums.AreaCorporalN1;
import com.ufrj.escalaiv2.enums.AreaCorporalN2;
import com.ufrj.escalaiv2.enums.AreaCorporalN3;

import com.ufrj.escalaiv2.model.PainReportItem;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.repository.UserDailyDataRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;

public class RelatoriosVM extends AndroidViewModel {

    private static final String TAG = "RelatoriosVM";
    private final UserDailyDataRepository repository;
    private final LiveData<List<UserDailyData>> last7DaysData;
    private final int currentUserId = 1;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public RelatoriosVM(Application application) {
        super(application);
        repository = new UserDailyDataRepository(application);

        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = sdf.format(cal.getTime());

        last7DaysData = repository.getDailyDataBetweenDatesLiveData(currentUserId, startDate, endDate);
    }

    public LiveData<List<UserDailyData>> getLast7DaysData() {
        return last7DaysData;
    }

    // --- LiveData para os gráficos existentes (Água, Sono, Treino, Humor) ---
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

    public LiveData<Map<String, Integer>> getTrainingDurationLast7Days() {
        return Transformations.map(last7DaysData, dataList -> {
            Map<String, Integer> trainingDataMap = new TreeMap<>();
            if (dataList != null) {
                for (UserDailyData data : dataList) {
                    int dailyTotalMinutes = 0;
                    if (data.getTreinosMap() != null) {
                        for (Integer duration : data.getTreinosMap().values()) {
                            if (duration != null) {
                                dailyTotalMinutes += duration;
                            }
                        }
                    }
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

    // --- LiveData para a Tabela de Dores ---
    public LiveData<List<PainReportItem>> getPainReportData() {
        return Transformations.map(last7DaysData, dataList -> {
            List<PainReportItem> reportItems = new ArrayList<>();
            if (dataList == null || dataList.isEmpty()) {
                return reportItems;
            }

            Map<String, List<PainDataPoint>> painDataByLocation = new HashMap<>();

            for (UserDailyData dailyData : dataList) {
                try {
                    Date date = sdf.parse(dailyData.getDate());
                    if (date == null) continue;

                    int intensity = dailyData.getIntensidadeDor();
                    // Se intensidade for 0 ou valor inválido, talvez pular o dia?
                    // if (intensity <= 0) continue; // Descomente se dor 0 não deve ser registrada

                    // Processa cada área de dor registrada no dia
                    processPainArea(painDataByLocation, date, intensity, dailyData.getAreaDorN1(), 1);
                    processPainArea(painDataByLocation, date, intensity, dailyData.getAreaDorN2(), 2);
                    processPainArea(painDataByLocation, date, intensity, dailyData.getAreaDorN3(), 3);

                } catch (ParseException e) {
                    Log.e(TAG, "Erro ao parsear data: " + dailyData.getDate(), e);
                }
            }

            Date today = getStartOfDay(new Date());

            for (Map.Entry<String, List<PainDataPoint>> entry : painDataByLocation.entrySet()) {
                String location = entry.getKey();
                List<PainDataPoint> dataPoints = entry.getValue();

                if (dataPoints.isEmpty()) continue;

                Collections.sort(dataPoints, Comparator.comparing(PainDataPoint::getDate));

                Date firstDate = dataPoints.get(0).getDate();
                Date lastDate = dataPoints.get(dataPoints.size() - 1).getDate();

                long diffInMilliesTodayLast = Math.abs(today.getTime() - getStartOfDay(lastDate).getTime());
                long daysSinceLastPain = TimeUnit.DAYS.convert(diffInMilliesTodayLast, TimeUnit.MILLISECONDS);

                if (daysSinceLastPain > 7) {
                    continue;
                }

                long diffInMilliesTodayFirst = Math.abs(today.getTime() - getStartOfDay(firstDate).getTime());
                long daysSinceFirstPain = TimeUnit.DAYS.convert(diffInMilliesTodayFirst, TimeUnit.MILLISECONDS);

                int maxIntensity = 0;
                int sumIntensity = 0;
                for (PainDataPoint point : dataPoints) {
                    if (point.getIntensity() > maxIntensity) {
                        maxIntensity = point.getIntensity();
                    }
                    sumIntensity += point.getIntensity();
                }
                float avgIntensity = (float) sumIntensity / dataPoints.size();

                String tempoDesdeInicioStr = formatDaysAgo(daysSinceFirstPain);
                String tempoSemDorStr = (daysSinceLastPain == 0) ? "Hoje" : formatDaysAgo(daysSinceLastPain);

                reportItems.add(new PainReportItem(location, tempoDesdeInicioStr, maxIntensity, avgIntensity, tempoSemDorStr));
            }

            Collections.sort(reportItems, Comparator.comparing(PainReportItem::getLocalDor));

            return reportItems;
        });
    }

    // Método auxiliar para processar cada área de dor
    private void processPainArea(Map<String, List<PainDataPoint>> painDataMap, Date date, int intensity, int areaId, int nivel) {
        if (areaId <= 0) return; // Assume que ID 0 ou negativo é inválido/não selecionado

        String locationName = getLocationNameById(areaId, nivel);
        if (locationName != null && !locationName.isEmpty()) {
            painDataMap.computeIfAbsent(locationName, k -> new ArrayList<>()).add(new PainDataPoint(date, intensity));
        }
    }

    // Método para obter o nome do local da dor pelo ID e nível
    private String getLocationNameById(int areaId, int nivel) {
        try {
            switch (nivel) {
                case 1:
                    AreaCorporalN1 areaN1 = AreaCorporalN1.getById(areaId);
                    return (areaN1 != null) ? areaN1.getNome() : null;
                case 2:
                    AreaCorporalN2 areaN2 = AreaCorporalN2.getById(areaId);
                    return (areaN2 != null) ? areaN2.getNome() : "Nível 2 - ID: " + areaId;
                case 3:
                    AreaCorporalN3 areaN3 = AreaCorporalN3.getById(areaId);
                    return (areaN3 != null) ? areaN3.getNome() : "Nível 3 - ID: " + areaId;
                default:
                    return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter nome da área de dor - Nível: " + nivel + ", ID: " + areaId, e);
            return "Erro ID: " + areaId;
        }
    }


    private static class PainDataPoint {
        private final Date date;
        private final int intensity;

        PainDataPoint(Date date, int intensity) {
            this.date = date;
            this.intensity = intensity;
        }

        Date getDate() {
            return date;
        }

        int getIntensity() {
            return intensity;
        }
    }

    private String formatDaysAgo(long days) {
        if (days == 0) return "Hoje";
        if (days == 1) return "1 dia";
        return days + " dias";
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}

