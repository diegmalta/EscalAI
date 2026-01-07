package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.VerifyEmailResponse;
import com.ufrj.escalaiv2.network.AuthApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailVM extends AndroidViewModel {
    private static final String TAG = "VerifyEmailVM";
    
    private final AuthApiService authApiService;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> verificationSuccess = new MutableLiveData<>(false);

    public VerifyEmailVM(Application application) {
        super(application);
        authApiService = RetrofitClient.getAuthApiService();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getVerificationSuccess() {
        return verificationSuccess;
    }

    public void verifyEmail(String email, String code) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Email não pode estar vazio");
            return;
        }

        if (code == null || code.trim().isEmpty()) {
            errorMessage.setValue("Código de verificação não pode estar vazio");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue("");
        successMessage.setValue("");

        Log.d(TAG, "Verificando email: " + email + " com código: " + code);

        authApiService.verifyEmail(email, code).enqueue(new Callback<VerifyEmailResponse>() {
            @Override
            public void onResponse(Call<VerifyEmailResponse> call, Response<VerifyEmailResponse> response) {
                isLoading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    VerifyEmailResponse verifyResponse = response.body();
                    
                    if (verifyResponse.isSuccess()) {
                        Log.d(TAG, "Verificação bem-sucedida: " + verifyResponse.getMessage());
                        successMessage.postValue(verifyResponse.getMessage());
                        verificationSuccess.postValue(true);
                    } else {
                        Log.e(TAG, "Erro na verificação: " + verifyResponse.getMessage());
                        errorMessage.postValue(verifyResponse.getMessage());
                    }
                } else {
                    String error = "Erro ao verificar email";
                    
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                            Log.e(TAG, "Erro do servidor: " + error);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar resposta", e);
                    }
                    
                    if (response.code() == 400) {
                        error = "Código inválido ou expirado";
                    } else if (response.code() == 404) {
                        error = "Usuário não encontrado";
                    }
                    
                    errorMessage.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<VerifyEmailResponse> call, Throwable t) {
                isLoading.postValue(false);
                String error = "Erro de conexão: " + t.getMessage();
                Log.e(TAG, "Falha na requisição", t);
                errorMessage.postValue(error);
            }
        });
    }
}



