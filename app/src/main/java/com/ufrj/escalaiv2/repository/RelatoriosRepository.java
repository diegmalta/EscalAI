package com.ufrj.escalaiv2.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.AguaResponse;
import com.ufrj.escalaiv2.dto.DadosUsuarioResponse;
import com.ufrj.escalaiv2.dto.DorResponse;
import com.ufrj.escalaiv2.dto.HumorResponse;
import com.ufrj.escalaiv2.dto.SonoResponse;
import com.ufrj.escalaiv2.dto.TreinoResponse;
import com.ufrj.escalaiv2.model.PainReportItem;
import com.ufrj.escalaiv2.model.TreinoReportItem;
import com.ufrj.escalaiv2.network.AtividadesApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatoriosRepository {
    private static final String TAG = "RelatoriosRepository";
    private final AtividadesApiService atividadesApiService;
    private final AuthRepository authRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public RelatoriosRepository(Application application) {
        this.atividadesApiService = RetrofitClient.getAtividadesApiService(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<DadosUsuarioResponse> getDadosUsuario(int dias) {
        MutableLiveData<DadosUsuarioResponse> result = new MutableLiveData<>();

        String token = authRepository.getAuthTokenRaw();
        if (token == null) {
            token = authRepository.getAuthToken();
        }
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        if (token == null) {
            Log.e(TAG, "Token de autenticação não encontrado");
            result.setValue(null);
            return result;
        }

        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d(TAG, "Data atual no app: " + hoje);
        Log.d(TAG, "Buscando dados dos últimos " + dias + " dias");
        atividadesApiService.getDadosUsuario(dias, token).enqueue(new Callback<DadosUsuarioResponse>() {
            @Override
            public void onResponse(Call<DadosUsuarioResponse> call, Response<DadosUsuarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DadosUsuarioResponse dados = response.body();
                    Log.d(TAG, "Dados do usuário obtidos com sucesso");
                    Log.d(TAG, "Período: " + dados.getDataInicio() + " até " + dados.getDataFim());
                    Log.d(TAG, "Água: " + (dados.getAgua() != null ? dados.getAgua().size() : 0) + " registros");
                    Log.d(TAG, "Sono: " + (dados.getSono() != null ? dados.getSono().size() : 0) + " registros");
                    Log.d(TAG, "Treinos: " + (dados.getTreinos() != null ? dados.getTreinos().size() : 0) + " registros");
                    Log.d(TAG, "Humor: " + (dados.getHumor() != null ? dados.getHumor().size() : 0) + " registros");
                    Log.d(TAG, "Dores: " + (dados.getDores() != null ? dados.getDores().size() : 0) + " registros");
                    result.setValue(dados);
                } else {
                    Log.e(TAG, "Erro ao obter dados do usuário: " + response.code());
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DadosUsuarioResponse> call, Throwable t) {
                Log.e(TAG, "Falha na requisição de dados do usuário: " + t.getMessage());
                result.setValue(null);
            }
        });

        return result;
    }

    // Métodos para processar os dados dos relatórios
    public LiveData<Map<String, Integer>> getWaterConsumptionData(DadosUsuarioResponse dados) {
        MutableLiveData<Map<String, Integer>> result = new MutableLiveData<>();

        if (dados == null || dados.getAgua() == null) {
            Log.d(TAG, "getWaterConsumptionData: dados nulos ou vazios");
            result.setValue(new TreeMap<>());
            return result;
        }

        Map<String, Integer> waterDataMap = new TreeMap<>();
        for (AguaResponse agua : dados.getAgua()) {
            Log.d(TAG, "Processando água: " + agua.getDate() + " - " + agua.getQuantity() + "ml");
            waterDataMap.put(agua.getDate(), agua.getQuantity());
        }

        Log.d(TAG, "getWaterConsumptionData: " + waterDataMap.size() + " dias de dados de água");
        result.setValue(waterDataMap);
        return result;
    }

    public LiveData<Map<String, Map<String, Integer>>> getSleepData(DadosUsuarioResponse dados) {
        MutableLiveData<Map<String, Map<String, Integer>>> result = new MutableLiveData<>();

        if (dados == null || dados.getSono() == null) {
            Log.d(TAG, "getSleepData: dados nulos ou vazios");
            result.setValue(new TreeMap<>());
            return result;
        }

        Map<String, Map<String, Integer>> sleepDataMap = new TreeMap<>();
        for (SonoResponse sono : dados.getSono()) {
            Log.d(TAG, "Processando sono: " + sono.getDate() + " - " + sono.getDurationMinutes() + "min - Qualidade: " + sono.getQuality());

            Map<String, Integer> dailySleep = new TreeMap<>();
            dailySleep.put("Duração", sono.getDurationMinutes());
            dailySleep.put("Qualidade", sono.getQuality());
            sleepDataMap.put(sono.getDate(), dailySleep);
        }

        Log.d(TAG, "getSleepData: " + sleepDataMap.size() + " dias de dados de sono");
        result.setValue(sleepDataMap);
        return result;
    }

    public LiveData<List<TreinoReportItem>> getTrainingReportData(DadosUsuarioResponse dados) {
        MutableLiveData<List<TreinoReportItem>> result = new MutableLiveData<>();

        if (dados == null || dados.getTreinos() == null) {
            Log.d(TAG, "getTrainingReportData: dados nulos ou vazios");
            result.setValue(new ArrayList<>());
            return result;
        }

        List<TreinoReportItem> reportItems = new ArrayList<>();
        for (TreinoResponse treino : dados.getTreinos()) {
            Log.d(TAG, "Processando treino: " + treino.getDate() + " - " + treino.getTipoTreino() + " - " + treino.getDurationMinutes() + "min");

            // Formatar data para exibição
            String dataFormatada = formatDateForDisplay(treino.getDate());

            reportItems.add(new TreinoReportItem(treino.getTipoTreino(), dataFormatada, treino.getDurationMinutes()));
        }

        // Ordenar por data (mais recente primeiro)
        Collections.sort(reportItems, (a, b) -> b.getData().compareTo(a.getData()));

        Log.d(TAG, "getTrainingReportData: " + reportItems.size() + " itens de relatório de treino");
        result.setValue(reportItems);
        return result;
    }

    public LiveData<Map<String, Map<String, Integer>>> getMoodLevelsData(DadosUsuarioResponse dados) {
        MutableLiveData<Map<String, Map<String, Integer>>> result = new MutableLiveData<>();

        if (dados == null || dados.getHumor() == null) {
            Log.d(TAG, "getMoodLevelsData: dados nulos ou vazios");
            result.setValue(new TreeMap<>());
            return result;
        }

        Map<String, Map<String, Integer>> moodDataMap = new TreeMap<>();
        for (HumorResponse humor : dados.getHumor()) {
            Map<String, Integer> dailyMoods = new TreeMap<>();
            dailyMoods.put("Alegria", humor.getJoyLevel());
            dailyMoods.put("Tristeza", humor.getSadnessLevel());
            dailyMoods.put("Ansiedade", humor.getAnxietyLevel());
            dailyMoods.put("Estresse", humor.getStressLevel());
            dailyMoods.put("Calma", humor.getCalmLevel());
            Log.d(TAG, "Processando humor: " + humor.getDate() + " - Alegria:" + humor.getJoyLevel() +
                    " Tristeza:" + humor.getSadnessLevel() + " Ansiedade:" + humor.getAnxietyLevel() +
                    " Estresse:" + humor.getStressLevel() + " Calma:" + humor.getCalmLevel());
            moodDataMap.put(humor.getDate(), dailyMoods);
        }

        Log.d(TAG, "getMoodLevelsData: " + moodDataMap.size() + " dias de dados de humor");
        result.setValue(moodDataMap);
        return result;
    }

    public LiveData<List<PainReportItem>> getPainReportData(DadosUsuarioResponse dados) {
        MutableLiveData<List<PainReportItem>> result = new MutableLiveData<>();

        if (dados == null || dados.getDores() == null) {
            result.setValue(new ArrayList<>());
            return result;
        }

        List<PainReportItem> reportItems = new ArrayList<>();
        Map<String, List<PainDataPoint>> painDataByLocation = new HashMap<>();

        for (DorResponse dor : dados.getDores()) {
            try {
                Date date = sdf.parse(dor.getDate());
                if (date == null) continue;

                int intensity = dor.getIntensidadeDor();
                if (intensity <= 0) continue;

                Log.d(TAG, "Processando dor: " + dor.getDate() + " - Intensidade:" + intensity +
                        " Área1:" + dor.getAreaDorN1() + " Área2:" + dor.getAreaDorN2() + " Área3:" + dor.getAreaDorN3());

                // Processa cada área de dor registrada
                processPainArea(painDataByLocation, date, intensity, dor.getAreaDorN1(), 1);
                processPainArea(painDataByLocation, date, intensity, dor.getAreaDorN2(), 2);
                processPainArea(painDataByLocation, date, intensity, dor.getAreaDorN3(), 3);

            } catch (ParseException e) {
                Log.e(TAG, "Erro ao parsear data: " + dor.getDate(), e);
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
        Log.d(TAG, "getPainReportData: " + reportItems.size() + " itens de relatório de dor");
        result.setValue(reportItems);
        return result;
    }

    // Métodos auxiliares
    private void processPainArea(Map<String, List<PainDataPoint>> painDataMap, Date date, int intensity, int areaId, int nivel) {
        if (areaId <= 0) return;

        String locationName = getLocationNameById(areaId, nivel);
        if (locationName != null && !locationName.isEmpty()) {
            painDataMap.computeIfAbsent(locationName, k -> new ArrayList<>()).add(new PainDataPoint(date, intensity));
        }
    }

    private String getLocationNameById(int areaId, int nivel) {
        try {
            switch (nivel) {
                case 1:
                    return com.ufrj.escalaiv2.enums.AreaCorporalN1.getById(areaId) != null ?
                            com.ufrj.escalaiv2.enums.AreaCorporalN1.getById(areaId).getNome() : null;
                case 2:
                    return com.ufrj.escalaiv2.enums.AreaCorporalN2.getById(areaId) != null ?
                            com.ufrj.escalaiv2.enums.AreaCorporalN2.getById(areaId).getNome() :
                            "Nível 2 - ID: " + areaId;
                case 3:
                    return com.ufrj.escalaiv2.enums.AreaCorporalN3.getById(areaId) != null ?
                            com.ufrj.escalaiv2.enums.AreaCorporalN3.getById(areaId).getNome() :
                            "Nível 3 - ID: " + areaId;
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

    private String formatDateForDisplay(String dateStr) {
        try {
            Date date = sdf.parse(dateStr);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return displayFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Erro ao formatar data: " + dateStr, e);
            return dateStr;
        }
    }
}