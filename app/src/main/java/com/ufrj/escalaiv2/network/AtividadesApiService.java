package com.ufrj.escalaiv2.network;

import com.ufrj.escalaiv2.dto.AguaRequest;
import com.ufrj.escalaiv2.dto.DorRequest;
import com.ufrj.escalaiv2.dto.HumorRequest;
import com.ufrj.escalaiv2.dto.SonoRequest;
import com.ufrj.escalaiv2.dto.TreinoRequest;
import com.ufrj.escalaiv2.dto.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AtividadesApiService {

    @POST("api/agua/")
    Call<ApiResponse> registrarAgua(@Header("Authorization") String token, @Body AguaRequest request);

    @POST("api/humor/")
    Call<ApiResponse> registrarHumor(@Header("Authorization") String token, @Body HumorRequest request);

    @POST("api/sono/")
    Call<ApiResponse> registrarSono(@Header("Authorization") String token, @Body SonoRequest request);

    @POST("api/treino/")
    Call<ApiResponse> registrarTreino(@Header("Authorization") String token, @Body TreinoRequest request);

    @POST("api/dor/")
    Call<ApiResponse> registrarDor(@Header("Authorization") String token, @Body DorRequest request);
}