package com.ufrj.escalaiv2.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.network.LesaoApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LesaoRepository {
    private static final String TAG = "LesaoRepository";
    private final LesaoApiService lesaoApiService;
    private final AuthRepository authRepository;
    private final Context context;

    public LesaoRepository(Context context) {
        this.context = context;
        this.lesaoApiService = RetrofitClient.getLesaoApiService();
        this.authRepository = new AuthRepository(context);
    }

    public LiveData<LesaoResponse> getUserLesoes(int userId) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();
        
        String token = "Bearer " + authRepository.getAuthToken();
        if (token == null || token.equals("Bearer null")) {
            LesaoResponse errorResponse = new LesaoResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Usuário não autenticado");
            result.setValue(errorResponse);
            return result;
        }
        
        lesaoApiService.getUserLesoes(userId, token).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    LesaoResponse errorResponse = new LesaoResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Erro ao buscar dados: " + 
                            (response.errorBody() != null ? response.code() : "Desconhecido"));
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                LesaoResponse errorResponse = new LesaoResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Falha na comunicação: " + t.getMessage());
                result.setValue(errorResponse);
                Log.e(TAG, "Falha na chamada de API", t);
            }
        });
        
        return result;
    }
    
    public LiveData<LesaoResponse> saveLesao(LesaoRequest lesaoRequest) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();
        
        String token = "Bearer " + authRepository.getAuthToken();
        if (token == null || token.equals("Bearer null")) {
            LesaoResponse errorResponse = new LesaoResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Usuário não autenticado");
            result.setValue(errorResponse);
            return result;
        }
        
        // Adiciona o token ao request
        lesaoRequest.setToken(authRepository.getAuthToken());
        
        Call<LesaoResponse> call;
        if (lesaoRequest.getUserId() > 0) {
            // Atualização de lesão existente
            call = lesaoApiService.updateLesao(lesaoRequest.getUserId(), lesaoRequest, token);
        } else {
            // Criação de nova lesão
            call = lesaoApiService.createLesao(lesaoRequest, token);
        }
        
        call.enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    LesaoResponse errorResponse = new LesaoResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Erro ao salvar dados: " + 
                            (response.errorBody() != null ? response.code() : "Desconhecido"));
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                LesaoResponse errorResponse = new LesaoResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Falha na comunicação: " + t.getMessage());
                result.setValue(errorResponse);
                Log.e(TAG, "Falha na chamada de API", t);
            }
        });
        
        return result;
    }
}
