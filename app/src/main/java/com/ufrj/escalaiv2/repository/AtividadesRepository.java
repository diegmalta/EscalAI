package com.ufrj.escalaiv2.repository;

import android.app.Application;
import android.util.Log;

import com.ufrj.escalaiv2.dto.AguaRequest;
import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.DorRequest;
import com.ufrj.escalaiv2.dto.HumorRequest;
import com.ufrj.escalaiv2.dto.SonoRequest;
import com.ufrj.escalaiv2.dto.TreinoRequest;
import com.ufrj.escalaiv2.enums.HumorValues;
import com.ufrj.escalaiv2.enums.TipoTreino;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.network.AtividadesApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;
import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.model.AppDatabase;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtividadesRepository {
    private final Context context;
    private final UserDailyDataRepository userDailyDataRepository;
    private final AtividadesApiService atividadesApiService;
    private final AuthRepository authRepository;
    private final ExecutorService executorService;

    public AtividadesRepository(Application application) {
        this.context = application.getApplicationContext();
        this.userDailyDataRepository = new UserDailyDataRepository(application);
        this.atividadesApiService = RetrofitClient.getAtividadesApiService(application);
        this.authRepository = new AuthRepository(application);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Método utilitário para obter o id do primeiro usuário local
    private int getLocalUserId(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        Usuario usuario = db.usuarioDao().getFirstUser();
        return usuario != null ? usuario.getId() : -1;
    }

    // Método para registrar água
    public void registrarAgua(int quantidade, String token, OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            userDailyDataRepository.addWaterConsumption(localUserId, quantidade);
            AguaRequest request = new AguaRequest(todayDate, quantidade);
            atividadesApiService.registrarAgua(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "Água registrada no servidor com sucesso");
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "Erro ao registrar água no servidor: " + response.code());
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "Falha na requisição de água: " + t.getMessage());
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    // Método para registrar humor
    public void registrarHumor(HumorValues joyLevel, HumorValues sadnessLevel,
                              HumorValues anxietyLevel, HumorValues stressLevel, HumorValues calmLevel, String token,
                              OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean localSuccess = userDailyDataRepository.saveMoodData(localUserId, joyLevel, sadnessLevel,
                    anxietyLevel, stressLevel, calmLevel);
            if (!localSuccess) {
                callback.onError("Erro ao salvar dados localmente");
                return;
            }
            HumorRequest request = new HumorRequest(todayDate, joyLevel.ordinal(),
                    sadnessLevel.ordinal(), anxietyLevel.ordinal(), stressLevel.ordinal(), calmLevel.ordinal());
            atividadesApiService.registrarHumor(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "Humor registrado no servidor com sucesso");
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "Erro ao registrar humor no servidor: " + response.code());
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "Falha na requisição de humor: " + t.getMessage());
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    // Método para registrar sono
    public void registrarSono(String date, String sleepTime, String wakeTime,
                             Integer totalSleepMinutes, Integer quality, String token, OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            boolean localSuccess = userDailyDataRepository.saveSleepData(localUserId, date, sleepTime,
                    wakeTime, totalSleepMinutes, quality);
            if (!localSuccess) {
                callback.onError("Erro ao salvar dados localmente");
                return;
            }
            SonoRequest request = new SonoRequest(date, sleepTime, wakeTime,
                    totalSleepMinutes != null ? totalSleepMinutes : 0,
                    quality != null ? quality : 3);
            atividadesApiService.registrarSono(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "Sono registrado no servidor com sucesso");
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "Erro ao registrar sono no servidor: " + response.code());
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "Falha na requisição de sono: " + t.getMessage());
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    // Método para registrar treino (salva localmente em thread de fundo e envia ao servidor)
    public void registrarTreino(int tipoTreinoIndex, int duracaoMinutos, String token, OnActivityCallback callback) {
        Log.d("AtividadesRepository", "registrarTreino - Iniciando registro: tipoIndex=" + tipoTreinoIndex + ", duracao=" + duracaoMinutos);
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            Log.d("AtividadesRepository", "registrarTreino - localUserId=" + localUserId);
            if (localUserId != -1) {
                boolean roomSuccess = userDailyDataRepository.saveTreinoData(localUserId, tipoTreinoIndex, duracaoMinutos);
                Log.d("AtividadesRepository", "registrarTreino - Salvamento no Room: " + (roomSuccess ? "SUCESSO" : "FALHA"));
            }

            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            TipoTreino tipoTreino = TipoTreino.getById(tipoTreinoIndex);
            Log.d("AtividadesRepository", "registrarTreino - Preparando envio ao servidor: tipo=" + tipoTreino.name() + ", data=" + todayDate);
            TreinoRequest request = new TreinoRequest(todayDate, tipoTreino.name(), duracaoMinutos);
            atividadesApiService.registrarTreino(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "registrarTreino - SERVIDOR RESPONDEU 200 - Treino registrado com sucesso!");
                        Log.d("AtividadesRepository", "registrarTreino - Resposta do servidor: " + response.body());
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "registrarTreino - ERRO no servidor: código=" + response.code());
                        try {
                            Log.e("AtividadesRepository", "registrarTreino - Corpo do erro: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("AtividadesRepository", "Erro ao ler corpo da resposta de erro", e);
                        }
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "registrarTreino - FALHA na conexão com servidor: " + t.getMessage(), t);
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    public void getTodayTrainingDataAsync(Integer tipoTreinoIndex, Integer duracaoMinutos, java.util.function.Consumer<Map<String, Float>> callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            Map<String, Float> map;
            if (localUserId == -1) {
                map = new HashMap<>();
            } else {
                map = userDailyDataRepository.getTodayTrainingData(localUserId);
            }

            // Se não há usuário local ou o mapa veio vazio, garantir inclusão do treino recém enviado
            if (tipoTreinoIndex != null && duracaoMinutos != null && duracaoMinutos > 0) {
                TipoTreino tipo = TipoTreino.getById(tipoTreinoIndex);
                if (tipo != null) {
                    float horas = duracaoMinutos / 60.0f;
                    map.put(tipo.getNome(), map.getOrDefault(tipo.getNome(), 0f) + horas);
                }
            }
            callback.accept(map);
        });
    }

    // Método para registrar dor
    public void registrarDor(int areaDor, int subareaDor, int especificacaoDor,
                           int intensidadeDor, String token, OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean localSuccess = userDailyDataRepository.saveDorData(localUserId, areaDor, subareaDor,
                    especificacaoDor, intensidadeDor);
            if (!localSuccess) {
                callback.onError("Erro ao salvar dados localmente");
                return;
            }
            DorRequest request = new DorRequest(todayDate, areaDor, subareaDor,
                    especificacaoDor, intensidadeDor);
            atividadesApiService.registrarDor(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "Dor registrada no servidor com sucesso");
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "Erro ao registrar dor no servidor: " + response.code());
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "Falha na requisição de dor: " + t.getMessage());
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    // Métodos para obter dados (apenas localmente)
    public int getTotalWaterConsumption() {
        int localUserId = getLocalUserId(context);
        if (localUserId == -1) return 0;
        return userDailyDataRepository.getTotalWaterConsumption(localUserId);
    }

    public UserDailyData getTodayMoodData() {
        int localUserId = getLocalUserId(context);
        if (localUserId == -1) return null;
        return userDailyDataRepository.getTodayMoodData(localUserId);
    }

    public UserDailyData getTodayDorData() {
        int localUserId = getLocalUserId(context);
        if (localUserId == -1) return null;
        return userDailyDataRepository.getTodayDorData(localUserId);
    }

    public Map<String, Float> getTodayTrainingData() {
        int localUserId = getLocalUserId(context);
        if (localUserId == -1) return new HashMap<>();
        return userDailyDataRepository.getTodayTrainingData(localUserId);
    }

    public void getTodayTrainingDataAsync(int tipoTreinoIndex, int duracaoMinutos, java.util.function.Consumer<Map<String, Float>> callback) {
        executorService.execute(() -> {
            Map<String, Float> resumoMap = new HashMap<>();
            try {
                Log.d("AtividadesRepository", "getTodayTrainingDataAsync - tipoIndex: " + tipoTreinoIndex + 
                        ", duracao: " + duracaoMinutos);
                int localUserId = getLocalUserId(context);
                Log.d("AtividadesRepository", "localUserId: " + localUserId);
                if (localUserId != -1) {
                    resumoMap = userDailyDataRepository.getTodayTrainingData(localUserId);
                    Log.d("AtividadesRepository", "Resumo do Room: " + resumoMap);
                }
                
                // Se não conseguiu ler do Room ou está vazio, adicionar o treino que acabou de salvar
                if (resumoMap.isEmpty()) {
                    Log.d("AtividadesRepository", "Resumo vazio, adicionando treino recém-salvo");
                    TipoTreino tipo = TipoTreino.getById(tipoTreinoIndex);
                    if (tipo != null) {
                        resumoMap.put(tipo.getNome(), duracaoMinutos / 60.0f);
                        Log.d("AtividadesRepository", "Adicionado: " + tipo.getNome() + " = " + (duracaoMinutos / 60.0f) + "h");
                    }
                }
            } catch (Exception e) {
                Log.e("AtividadesRepository", "Erro ao buscar resumo de treinos", e);
            }
            final Map<String, Float> finalMap = resumoMap;
            Log.d("AtividadesRepository", "Chamando callback com: " + finalMap);
            callback.accept(finalMap);
        });
    }

    public void resetWaterConsumption() {
        int localUserId = getLocalUserId(context);
        if (localUserId != -1) {
            userDailyDataRepository.resetWaterConsumption(localUserId);
        }
    }

    public void getCurrentUserId(java.util.function.Consumer<Integer> callback) {
        authRepository.getCurrentUserIdAsync(callback);
    }

    // Interface de callback
    public interface OnActivityCallback {
        void onSuccess();
        void onError(String message);
    }
}