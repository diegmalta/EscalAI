package com.ufrj.escalaiv2.network;

import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.LesaoRequest;
import com.ufrj.escalaiv2.dto.LesaoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LesaoApiService {
    
    @GET("api/lesoes/user/{userId}")
    Call<LesaoResponse> getUserLesoes(
        @Path("userId") int userId,
        @Header("Authorization") String token
    );
    
    @POST("api/lesoes")
    Call<LesaoResponse> createLesao(
        @Body LesaoRequest lesaoRequest,
        @Header("Authorization") String token
    );
    
    @PUT("api/lesoes/{id}")
    Call<LesaoResponse> updateLesao(
        @Path("id") int id,
        @Body LesaoRequest lesaoRequest,
        @Header("Authorization") String token
    );
}
