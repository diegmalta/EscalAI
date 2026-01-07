package com.ufrj.escalaiv2.network;

import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.RegisterRequest;
import com.ufrj.escalaiv2.dto.LoginRequest;
import com.ufrj.escalaiv2.dto.LoginResponse;
import com.ufrj.escalaiv2.dto.RefreshTokenRequest;
import com.ufrj.escalaiv2.dto.RefreshTokenResponse;
import com.ufrj.escalaiv2.dto.UserProfileResponse;
import com.ufrj.escalaiv2.dto.VerifyEmailRequest;
import com.ufrj.escalaiv2.dto.VerifyEmailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;
// Importe outras anotações HTTP (GET, PUT, DELETE) e @Path, @Query, @Header conforme necessário

public interface AuthApiService {

    @POST("auth/register") // Endpoint de cadastro
    Call<ApiResponse<Void>> registerUser(@Body RegisterRequest registerRequest);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/refresh")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest request);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Body RefreshTokenRequest request);

    @GET("api/user/profile")
    Call<UserProfileResponse> getUserProfile();

    @POST("auth/verify-email")
    Call<VerifyEmailResponse> verifyEmail(@Query("email") String email, @Query("code") String code);

    // forgot password
}
