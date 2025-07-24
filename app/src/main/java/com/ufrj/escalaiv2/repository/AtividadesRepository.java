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
        this.atividadesApiService = RetrofitClient.getAtividadesApiService();
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
    public void registrarAgua(int userId, int quantidade, String token, OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            userDailyDataRepository.addWaterConsumption(localUserId, quantidade);
            AguaRequest request = new AguaRequest(localUserId, todayDate, quantidade);
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
    public void registrarHumor(int userId, HumorValues joyLevel, HumorValues sadnessLevel,
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
            HumorRequest request = new HumorRequest(localUserId, todayDate, joyLevel.ordinal(),
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
    public void registrarSono(int userId, String date, String sleepTime, String wakeTime,
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
            SonoRequest request = new SonoRequest(localUserId, date, sleepTime, wakeTime,
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

    // Método para registrar treino
    public void registrarTreino(int userId, int tipoTreinoIndex, int duracaoMinutos, String token, OnActivityCallback callback) {
        executorService.execute(() -> {
            int localUserId = getLocalUserId(context);
            if (localUserId == -1) {
                callback.onError("Usuário local não encontrado");
                return;
            }
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean localSuccess = userDailyDataRepository.saveTreinoData(localUserId, tipoTreinoIndex, duracaoMinutos);
            if (!localSuccess) {
                callback.onError("Erro ao salvar dados localmente");
                return;
            }
            TipoTreino tipoTreino = TipoTreino.getById(tipoTreinoIndex);
            TreinoRequest request = new TreinoRequest(localUserId, todayDate, tipoTreino.name(), duracaoMinutos);
            atividadesApiService.registrarTreino(token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("AtividadesRepository", "Treino registrado no servidor com sucesso");
                        callback.onSuccess();
                    } else {
                        Log.e("AtividadesRepository", "Erro ao registrar treino no servidor: " + response.code());
                        callback.onError("Erro ao enviar dados para o servidor");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("AtividadesRepository", "Falha na requisição de treino: " + t.getMessage());
                    callback.onError("Erro de conexão com o servidor");
                }
            });
        });
    }

    // Método para registrar dor
    public void registrarDor(int userId, int areaDor, int subareaDor, int especificacaoDor,
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
            DorRequest request = new DorRequest(localUserId, todayDate, areaDor, subareaDor,
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
    public int getTotalWaterConsumption(int userId) {
        return userDailyDataRepository.getTotalWaterConsumption(userId);
    }

    public UserDailyData getTodayMoodData(int userId) {
        return userDailyDataRepository.getTodayMoodData(userId);
    }

    public UserDailyData getTodayDorData(int userId) {
        return userDailyDataRepository.getTodayDorData(userId);
    }

    public Map<String, Float> getTodayTrainingData(int userId) {
        return userDailyDataRepository.getTodayTrainingData(userId);
    }

    public void resetWaterConsumption(int userId) {
        userDailyDataRepository.resetWaterConsumption(userId);
    }

    public int getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    // Interface de callback
    public interface OnActivityCallback {
        void onSuccess();
        void onError(String message);
    }
}