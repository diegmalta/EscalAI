package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.LoginResponse;
import com.ufrj.escalaiv2.repository.AuthRepository;

/**
 * ViewModel para a tela de login, gerenciando a autenticação via API.
 */
public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        // Passa o contexto da aplicação para o AuthRepository para permitir o uso do EncryptedSharedPreferences
        authRepository = new AuthRepository(application);
    }

    /**
     * Verifica se o usuário já está autenticado com um token válido.
     *
     * @return true se o usuário estiver autenticado e o token for válido
     */
    public boolean isAuthenticated() {
        return authRepository.isAuthenticated();
    }

    /**
     * Realiza o login via API.
     *
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return LiveData contendo a resposta da API
     */
    public LiveData<ApiResponse<LoginResponse>> login(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Validação básica
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            isLoading.setValue(false);
            errorMessage.setValue("Email e senha são obrigatórios");

            // Retorna um LiveData com erro
            MutableLiveData<ApiResponse<LoginResponse>> errorResult = new MutableLiveData<>();
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Email e senha são obrigatórios");
            errorResult.setValue(errorResponse);
            return errorResult;
        }

        // Chama o repositório para fazer login
        LiveData<ApiResponse<LoginResponse>> result = authRepository.login(email, password);

        // Observa o resultado para atualizar o estado de carregamento
        result.observeForever(response -> {
            isLoading.setValue(false);
            if (response != null && !response.isSuccess()) {
                errorMessage.setValue(response.getMessage());
            }
        });

        return result;
    }

    /**
     * Realiza o logout do usuário.
     */
    public void logout() {
        authRepository.logout();
    }

    /**
     * Atualiza o timestamp de último uso do token.
     * Deve ser chamado quando o usuário realiza alguma ação autenticada.
     */
    public void updateLastUsedTimestamp() {
        authRepository.updateLastUsedTimestamp();
    }

    /**
     * Obtém o estado de carregamento.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Obtém a mensagem de erro.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
