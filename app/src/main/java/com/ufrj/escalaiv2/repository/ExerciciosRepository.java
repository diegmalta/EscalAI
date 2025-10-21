package com.ufrj.escalaiv2.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.AreaCorporalN1Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN2Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN3Response;
import com.ufrj.escalaiv2.dto.ExercicioResponse;
import com.ufrj.escalaiv2.dto.TiposResponse;
import com.ufrj.escalaiv2.network.ExerciciosApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciciosRepository {
    private static final String TAG = "ExerciciosRepository";
    private final ExerciciosApiService apiService;

    public ExerciciosRepository(Context context) {
        this.apiService = RetrofitClient.getExerciciosApiService(context);
    }

    public LiveData<List<ExercicioResponse>> listarExercicios(
            String tipo,
            Integer n1_id,
            Integer n2_id,
            Integer n3_id,
            String dificuldade,
            int limit,
            int offset
    ) {
        MutableLiveData<List<ExercicioResponse>> liveData = new MutableLiveData<>();

        apiService.listarExercicios(tipo, n1_id, n2_id, n3_id, dificuldade, limit, offset)
                .enqueue(new Callback<List<ExercicioResponse>>() {
                    @Override
                    public void onResponse(Call<List<ExercicioResponse>> call, Response<List<ExercicioResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(response.body());
                        } else {
                            Log.e(TAG, "Erro ao listar exercícios: " + response.code());
                            liveData.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ExercicioResponse>> call, Throwable t) {
                        Log.e(TAG, "Falha ao listar exercícios", t);
                        liveData.postValue(null);
                    }
                });

        return liveData;
    }

    public LiveData<ExercicioResponse> obterExercicio(int exercicioId) {
        MutableLiveData<ExercicioResponse> liveData = new MutableLiveData<>();

        apiService.obterExercicio(exercicioId).enqueue(new Callback<ExercicioResponse>() {
            @Override
            public void onResponse(Call<ExercicioResponse> call, Response<ExercicioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao obter exercício: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ExercicioResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao obter exercício", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public LiveData<Boolean> darLike(int exercicioId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        apiService.darLike(exercicioId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                liveData.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Falha ao dar like", t);
                liveData.postValue(false);
            }
        });

        return liveData;
    }

    public LiveData<List<String>> listarTipos() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        apiService.listarTipos().enqueue(new Callback<TiposResponse>() {
            @Override
            public void onResponse(Call<TiposResponse> call, Response<TiposResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getTipos());
                } else {
                    Log.e(TAG, "Erro ao listar tipos: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<TiposResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao listar tipos", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public LiveData<List<AreaCorporalN1Response>> listarAreasN1() {
        MutableLiveData<List<AreaCorporalN1Response>> liveData = new MutableLiveData<>();

        apiService.listarAreasN1().enqueue(new Callback<List<AreaCorporalN1Response>>() {
            @Override
            public void onResponse(Call<List<AreaCorporalN1Response>> call, Response<List<AreaCorporalN1Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao listar áreas N1: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AreaCorporalN1Response>> call, Throwable t) {
                Log.e(TAG, "Falha ao listar áreas N1", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public LiveData<List<AreaCorporalN2Response>> listarAreasN2(Integer n1_id) {
        MutableLiveData<List<AreaCorporalN2Response>> liveData = new MutableLiveData<>();

        apiService.listarAreasN2(n1_id).enqueue(new Callback<List<AreaCorporalN2Response>>() {
            @Override
            public void onResponse(Call<List<AreaCorporalN2Response>> call, Response<List<AreaCorporalN2Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao listar áreas N2: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AreaCorporalN2Response>> call, Throwable t) {
                Log.e(TAG, "Falha ao listar áreas N2", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public LiveData<List<AreaCorporalN3Response>> listarAreasN3(Integer n2_id) {
        MutableLiveData<List<AreaCorporalN3Response>> liveData = new MutableLiveData<>();

        apiService.listarAreasN3(n2_id).enqueue(new Callback<List<AreaCorporalN3Response>>() {
            @Override
            public void onResponse(Call<List<AreaCorporalN3Response>> call, Response<List<AreaCorporalN3Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao listar áreas N3: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AreaCorporalN3Response>> call, Throwable t) {
                Log.e(TAG, "Falha ao listar áreas N3", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}

