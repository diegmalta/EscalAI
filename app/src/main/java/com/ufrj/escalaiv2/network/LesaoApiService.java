package com.ufrj.escalaiv2.network;

import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoRequest;
import com.ufrj.escalaiv2.dto.PrevisaoAfastamentoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;

public interface LesaoApiService {
    @POST("api/lesoes")
    Call<LesaoResponse> saveLesao(@Body LesaoRequest request);

    @GET("api/lesoes/user/{user_id}")
    Call<LesaoResponse> getUserLesoes(@Path("user_id") int userId);

    @GET("api/lesoes/{id}")
    Call<LesaoResponse> getLesaoById(@Path("id") int lesaoId);

    @POST("engajamento/lesao/prever-afastamento-ml")
    Call<PrevisaoAfastamentoResponse> preverAfastamento(@Body PrevisaoAfastamentoRequest request);

    @PUT("api/lesoes/{id}")
    Call<LesaoResponse> updateLesao(@Path("id") int id, @Body LesaoRequest request);
}
