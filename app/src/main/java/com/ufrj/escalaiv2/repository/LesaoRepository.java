package com.ufrj.escalaiv2.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoRequest;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoResponse;
import com.ufrj.escalaiv2.network.LesaoApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LesaoRepository {
    private final LesaoApiService lesaoApiService;
    private final AuthRepository authRepository;
    private static final String TAG = "LesaoRepository";

    public LesaoRepository(Context context) {
        this.lesaoApiService = RetrofitClient.getLesaoApiService(context);
        this.authRepository = new AuthRepository(context);
    }

    public LiveData<LesaoResponse> saveLesao(LesaoRequest request) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.saveLesao(request).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao salvar lesão: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao salvar lesão", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<LesaoResponse> updateLesao(int id, LesaoRequest request) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.updateLesao(id, request).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao atualizar lesão: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao atualizar lesão", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<LesaoResponse> getUserLesoes() {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.getUserLesoes().enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao buscar lesões: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao buscar lesões", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<LesaoResponse> getLesaoById(int lesaoId) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.getLesaoById(lesaoId).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao buscar lesão: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao buscar lesão", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<PrevisaoAfastamentoResponse> preverAfastamento(PrevisaoAfastamentoRequest request) {
        MutableLiveData<PrevisaoAfastamentoResponse> result = new MutableLiveData<>();

        Log.d(TAG, "Usando API remota para previsão");
        lesaoApiService.preverAfastamento(request).enqueue(new Callback<PrevisaoAfastamentoResponse>() {
            @Override
            public void onResponse(Call<PrevisaoAfastamentoResponse> call, Response<PrevisaoAfastamentoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao prever afastamento: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<PrevisaoAfastamentoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao prever afastamento", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<LesaoResponse> concluirLesao(int id) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.concluirLesao(id).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao concluir lesão: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao concluir lesão", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<LesaoResponse> reabrirLesao(int id, LesaoRequest request) {
        // Para reabrir, atualizamos a lesão removendo a data_conclusao
        // Isso é feito através do updateLesao, mas com data_conclusao = null
        return updateLesao(id, request);
    }

    public LiveData<LesaoResponse> deleteLesao(int id) {
        MutableLiveData<LesaoResponse> result = new MutableLiveData<>();

        lesaoApiService.deleteLesao(id).enqueue(new Callback<LesaoResponse>() {
            @Override
            public void onResponse(Call<LesaoResponse> call, Response<LesaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao excluir lesão: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LesaoResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao excluir lesão", t);
                result.postValue(null);
            }
        });

        return result;
    }
}
