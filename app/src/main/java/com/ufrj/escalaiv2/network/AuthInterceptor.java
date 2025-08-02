package com.ufrj.escalaiv2.network;

import android.content.Context;
import android.util.Log;

import com.ufrj.escalaiv2.dto.RefreshTokenRequest;
import com.ufrj.escalaiv2.dto.RefreshTokenResponse;
import com.ufrj.escalaiv2.repository.AuthRepository;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * Interceptor para adicionar automaticamente o token de autenticação
 * e renovar quando necessário.
 */
public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";
    private final AuthRepository authRepository;
    private final AuthApiService authApiService;

    public AuthInterceptor(Context context) {
        this.authRepository = new AuthRepository(context);
        this.authApiService = RetrofitClient.getAuthApiService();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Se não é uma requisição de autenticação, adiciona o token
        if (!isAuthRequest(originalRequest.url().toString())) {
            String token = authRepository.getAuthToken();

            if (token != null) {
                // Adiciona o token ao header
                Request authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();

                Response response = chain.proceed(authenticatedRequest);

                // Se recebeu 401 (Unauthorized), tenta renovar o token
                if (response.code() == 401) {
                    Log.d(TAG, "Token expirado, tentando renovar...");

                    // Fecha a resposta atual
                    response.close();

                    // Tenta renovar o token
                    if (refreshToken()) {
                        // Pega o novo token
                        String newToken = authRepository.getAuthToken();
                        if (newToken != null) {
                            // Refaz a requisição com o novo token
                            Request newRequest = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer " + newToken)
                                    .build();

                            Log.d(TAG, "Token renovado, refazendo requisição");
                            return chain.proceed(newRequest);
                        }
                    }

                    // Se não conseguiu renovar, retorna o erro 401
                    Log.w(TAG, "Não foi possível renovar o token");
                }

                return response;
            }
        }

        // Se não há token ou é uma requisição de auth, prossegue normalmente
        return chain.proceed(originalRequest);
    }

    /**
     * Verifica se a requisição é para endpoints de autenticação.
     */
    private boolean isAuthRequest(String url) {
        return url.contains("/auth/login") ||
               url.contains("/auth/register") ||
               url.contains("/auth/refresh") ||
               url.contains("/auth/logout");
    }

    /**
     * Tenta renovar o token de forma síncrona.
     */
    private boolean refreshToken() {
        try {
            String refreshToken = authRepository.getRefreshToken();
            if (refreshToken == null || !authRepository.hasValidRefreshToken()) {
                Log.w(TAG, "Refresh token não disponível ou expirado");
                return false;
            }

            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
            Call<RefreshTokenResponse> call = authApiService.refreshToken(request);
            retrofit2.Response<RefreshTokenResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                RefreshTokenResponse refreshResponse = response.body();
                long expiresIn = refreshResponse.getExpiresIn();
                if (expiresIn <= 0) {
                    expiresIn = 3600;
                }

                // Salva o novo token
                authRepository.saveAuthToken(refreshResponse.getAccessToken(), expiresIn, refreshToken);
                Log.d(TAG, "Token renovado com sucesso");
                return true;
            } else {
                Log.w(TAG, "Falha ao renovar token: " + response.code());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao renovar token", e);
            return false;
        }
    }
}