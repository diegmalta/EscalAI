package com.ufrj.escalaiv2.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.LoginRequest;
import com.ufrj.escalaiv2.dto.LoginResponse;
import com.ufrj.escalaiv2.dto.RegisterRequest;
import com.ufrj.escalaiv2.dto.RefreshTokenRequest;
import com.ufrj.escalaiv2.dto.RefreshTokenResponse;
import com.ufrj.escalaiv2.dto.UserProfileResponse;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.network.AuthApiService;
import com.ufrj.escalaiv2.network.RetrofitClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositório para lidar com operações de autenticação (Cadastro, Login, etc.)
 * Interage com a AuthApiService e gerencia armazenamento local dos dados do usuário.
 */
public class AuthRepository {

    private static final String TAG = "AuthRepository";
    private static final String PREF_FILE_NAME = "auth_secure_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_LAST_USED = "last_used_timestamp";
    private static final String KEY_EXPIRY = "token_expiry";
    private static final String KEY_REFRESH_EXPIRY = "refresh_token_expiry";
    private static final String KEY_USER_ID = "user_id";
    private static final long THREE_DAYS_MS = 3 * 24 * 60 * 60 * 1000L; // 3 dias em milissegundos
    private static final long SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000L; // 7 dias em milissegundos

    private final AuthApiService authApiService;
    private Context context;
    private EncryptedSharedPreferences encryptedPrefs;
    private AppDatabase appDatabase;
    private final ExecutorService executorService;

    // Construtor sem contexto (para compatibilidade com código existente)
    public AuthRepository() {
        this.authApiService = RetrofitClient.getAuthApiService();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Construtor com contexto (para funcionalidades de EncryptedSharedPreferences e Room)
    public AuthRepository(Context context) {
        this.context = context;
        this.authApiService = RetrofitClient.getAuthApiService();
        this.appDatabase = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
        initEncryptedPrefs();
    }

    private void initEncryptedPrefs() {
        if (context == null) {
            Log.e(TAG, "Contexto não disponível para inicializar EncryptedSharedPreferences");
            return;
        }

        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedPrefs = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erro ao inicializar EncryptedSharedPreferences", e);
        }
    }

    /**
     * Tenta registrar um novo usuário na API.
     *
     * @param email O email do usuário.
     * @param nome O nome do usuário.
     * @param sobrenome O sobrenome do usuário.
     * @param dataNascimento A data de nascimento do usuário.
     * @param password A senha do usuário.
     * @return LiveData contendo a resposta da API (sucesso/falha e mensagem).
     */
    public LiveData<ApiResponse<Void>> registerUser(String email, String nome, String sobrenome, String celular, String dataNascimento, String password) {
        MutableLiveData<ApiResponse<Void>> result = new MutableLiveData<>();
        RegisterRequest request = new RegisterRequest(email, nome, sobrenome, celular, dataNascimento, password);

        authApiService.registerUser(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    Log.i(TAG, "Registro bem-sucedido: " + response.body().getMessage());
                } else {
                    // A API respondeu com erro (não-2xx) ou corpo vazio
                    String errorMessage = "Erro desconhecido no registro";
                    if (response.errorBody() != null) {
                        try {
                            // Tenta parsear a mensagem de erro do corpo da resposta
                            errorMessage = "Erro " + response.code() + ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody", e);
                            errorMessage = "Erro " + response.code() + " ao registrar";
                        }
                    } else if (response.message() != null && !response.message().isEmpty()){
                        errorMessage = "Erro " + response.code() + ": " + response.message();
                    }
                    ApiResponse<Void> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage(errorMessage);
                    result.setValue(errorResponse);
                    Log.w(TAG, "Falha no registro: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Falha na comunicação com a API (rede, timeout, etc.)
                ApiResponse<Void> failureResponse = new ApiResponse<>();
                failureResponse.setSuccess(false);
                failureResponse.setMessage("Falha na comunicação: " + t.getMessage());
                result.setValue(failureResponse);
                Log.e(TAG, "Falha na chamada de registro", t);
            }
        });

        return result;
    }

    /**
     * Tenta fazer login na API e salva os dados do usuário localmente.
     *
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return LiveData contendo a resposta da API
     */
    public LiveData<ApiResponse<LoginResponse>> login(String email, String password) {
        MutableLiveData<ApiResponse<LoginResponse>> result = new MutableLiveData<>();
        LoginRequest request = new LoginRequest(email, password);

        authApiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login bem-sucedido
                    LoginResponse loginResponse = response.body();

                    Log.d(TAG, "Dados do login recebidos: " + loginResponse);
                    Log.d(TAG, "Token recebido: " + loginResponse.getToken());
                    Log.d(TAG, "ExpiresIn: " + loginResponse.getExpiresIn());

                    long expiresIn = loginResponse.getExpiresIn();
                    if (expiresIn <= 0) {
                        Log.w(TAG, "expiresIn inválido (" + expiresIn + "), usando valor padrão de 3600 segundos (1 hora)");
                        expiresIn = 3600;
                    }

                    if (context != null) {
                        Log.d(TAG, "Contexto disponível, salvando token...");
                        saveAuthToken(loginResponse.getToken(), expiresIn, loginResponse.getRefreshToken());
                        saveUserDataToRoom(loginResponse.getUser());
                    } else {
                        Log.e(TAG, "Contexto é null, não foi possível salvar o token");
                    }

                    // Criar ApiResponse para manter compatibilidade
                    ApiResponse<LoginResponse> apiResponse = new ApiResponse<>();
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Login realizado com sucesso");
                    apiResponse.setData(loginResponse);

                    result.setValue(apiResponse);
                    Log.i(TAG, "Login bem-sucedido");
                } else {
                    // Erro na resposta da API
                    int statusCode = response.code();
                    String errorMessage = "Erro desconhecido no login";

                    if (statusCode == 403) {
                        errorMessage = "Email não verificado. Por favor, verifique seu email.";
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = "Erro " + statusCode + ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody", e);
                            errorMessage = "Erro " + statusCode + " ao fazer login";
                        }
                    } else if (response.message() != null && !response.message().isEmpty()){
                        errorMessage = "Erro " + statusCode + ": " + response.message();
                    }

                    ApiResponse<LoginResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage(errorMessage);
                    errorResponse.setHttpStatusCode(statusCode);
                    result.setValue(errorResponse);
                    Log.w(TAG, "Erro no login (" + statusCode + "): " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Falha na comunicação com a API
                ApiResponse<LoginResponse> failureResponse = new ApiResponse<>();
                failureResponse.setSuccess(false);
                failureResponse.setMessage("Falha na comunicação com o servidor: " + t.getMessage());
                result.setValue(failureResponse);
                Log.e(TAG, "Falha na chamada de login", t);
            }
        });

        return result;
    }

    /**
     * Salva os dados do usuário no banco de dados local (Room).
     *
     * @param userData Dados do usuário recebidos da API
     */
    private void saveUserDataToRoom(LoginResponse.UserData userData) {
        if (appDatabase == null || userData == null) {
            Log.e(TAG, "Não foi possível salvar dados do usuário: banco de dados ou dados nulos");
            return;
        }

        executorService.execute(() -> {
            try {
                // Verifica se o usuário já existe no banco
                Usuario existingUser = appDatabase.usuarioDao().getUserById((int) userData.getId());

                Usuario user;
                if (existingUser != null) {
                    // Atualiza o usuário existente
                    user = existingUser;
                } else {
                    // Cria um novo usuário
                    user = new Usuario();
                    user.setId((int) userData.getId());
                }

                // Atualiza os dados do usuário
                user.setEmail(userData.getEmail());
                user.setNome(userData.getName());
                user.setGender(userData.getGender());
                user.setPeso(userData.getWeight());
                user.setAltura(userData.getHeight());

                // Atualiza os graus de escalada

                user.setLastUpdateDate(new Date());

                // Salva ou atualiza o usuário no banco
                if (existingUser != null) {
                    appDatabase.usuarioDao().updateUser(user);
                    Log.d(TAG, "Usuário atualizado no banco de dados local");
                } else {
                    appDatabase.usuarioDao().insertUser(user);
                    Log.d(TAG, "Usuário inserido no banco de dados local");
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao salvar dados do usuário no banco local", e);
            }
        });
    }


    /**
     * Obtém os dados do usuário do banco de dados local.
     *
     * @param userId ID do usuário
     * @return LiveData contendo o usuário
     */
    public LiveData<Usuario> getUserFromRoom(int userId) {
        if (appDatabase == null) {
            Log.e(TAG, "Banco de dados não inicializado");
            return new MutableLiveData<>(null);
        }

        return appDatabase.usuarioDao().getUserByIdLiveData(userId);
    }

    /**
     * Salva o token de autenticação e refresh token de forma segura.
     *
     * @param token Token JWT recebido da API
     * @param expiresIn Tempo de expiração do token em segundos
     * @param refreshToken Refresh token recebido da API
     */
    public void saveAuthToken(String token, long expiresIn, String refreshToken) {
        if (encryptedPrefs == null) {
            Log.e(TAG, "EncryptedSharedPreferences não inicializado");
            return;
        }

        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + (expiresIn * 1000); // Converte segundos para milissegundos
        long refreshExpiryTime = currentTime + SEVEN_DAYS_MS; // 7 dias para refresh token

        Log.d(TAG, "Salvando token. expiresIn: " + expiresIn + " (segundos), expiryTime: " + expiryTime + " (ms), currentTime: " + currentTime);

        encryptedPrefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putLong(KEY_LAST_USED, currentTime)
                .putLong(KEY_EXPIRY, expiryTime)
                .putLong(KEY_REFRESH_EXPIRY, refreshExpiryTime)
                .apply();

        Log.d(TAG, "Token e refresh token salvos com sucesso. Expira em: " + new Date(expiryTime));
        Log.d(TAG, "Token salvo: " + token);
        Log.d(TAG, "Refresh token salvo: " + refreshToken);
        Log.d(TAG, "Expira em: " + new Date(expiryTime));
        Log.d(TAG, "Refresh token expira em: " + new Date(refreshExpiryTime));

        // Verificar se foi salvo corretamente
        String savedToken = encryptedPrefs.getString(KEY_TOKEN, null);
        String savedRefreshToken = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null);
        Log.d(TAG, "Token verificado após salvar: " + savedToken);
        Log.d(TAG, "Refresh token verificado após salvar: " + savedRefreshToken);
        Log.d(TAG, "=== FIM SAVE AUTH TOKEN DEBUG ===");
    }

    /**
     * Salva apenas o access token (para compatibilidade com código existente).
     */
    private void saveAuthToken(String token, long expiresIn) {
        saveAuthToken(token, expiresIn, null);
    }

    /**
     * Verifica se o token existe no armazenamento (independente de expiração).
     *
     * @return true se o token existe
     */
    public boolean hasToken() {
        if (encryptedPrefs == null) return false;
        String token = encryptedPrefs.getString(KEY_TOKEN, null);
        return token != null;
    }

    /**
     * Obtém o token de autenticação armazenado (sem verificar expiração).
     *
     * @return O token JWT ou null se não existir
     */
    public String getAuthTokenRaw() {
        if (encryptedPrefs == null) return null;
        return encryptedPrefs.getString(KEY_TOKEN, null);
    }

    /**
     * Obtém o refresh token armazenado.
     *
     * @return O refresh token ou null se não existir
     */
    public String getRefreshToken() {
        if (encryptedPrefs == null) return null;
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Verifica se o refresh token existe e não expirou.
     *
     * @return true se o refresh token for válido
     */
    public boolean hasValidRefreshToken() {
        if (encryptedPrefs == null) return false;

        String refreshToken = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null);
        if (refreshToken == null) return false;

        long refreshExpiryTime = encryptedPrefs.getLong(KEY_REFRESH_EXPIRY, 0);
        long currentTime = System.currentTimeMillis();

        return currentTime < refreshExpiryTime;
    }

    /**
     * Obtém o token de autenticação armazenado.
     *
     * @return O token JWT ou null se não estiver autenticado
     */
    public String getAuthToken() {
        if (encryptedPrefs == null) {
            Log.e(TAG, "EncryptedSharedPreferences não inicializado");
            return null;
        }

        String token = encryptedPrefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Token encontrado: " + (token != null ? "SIM" : "NÃO"));

        if (token == null) {
            Log.d(TAG, "Token é null");
            return null;
        }

        boolean isAuth = isAuthenticated();
        Log.d(TAG, "isAuthenticated(): " + isAuth);

        if (!isAuth) {
            Log.d(TAG, "Token existe mas não está autenticado");
            return null;
        }

        return token;
    }

    /**
     * Atualiza o timestamp de último uso do token.
     * Deve ser chamado sempre que o usuário realiza alguma ação autenticada.
     */
    public void updateLastUsedTimestamp() {
        if (encryptedPrefs == null) return;

        encryptedPrefs.edit()
                .putLong(KEY_LAST_USED, System.currentTimeMillis())
                .apply();
    }

    /**
     * Tenta renovar o access token usando o refresh token.
     *
     * @return LiveData contendo a resposta da API
     */
    public LiveData<ApiResponse<RefreshTokenResponse>> refreshToken() {
        MutableLiveData<ApiResponse<RefreshTokenResponse>> result = new MutableLiveData<>();

        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            ApiResponse<RefreshTokenResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Refresh token não encontrado");
            result.setValue(errorResponse);
            return result;
        }

        if (!hasValidRefreshToken()) {
            ApiResponse<RefreshTokenResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Refresh token expirado");
            result.setValue(errorResponse);
            return result;
        }

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        authApiService.refreshToken(request).enqueue(new Callback<RefreshTokenResponse>() {
            @Override
            public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Refresh bem-sucedido
                    RefreshTokenResponse refreshResponse = response.body();

                    Log.d(TAG, "Refresh token bem-sucedido");
                    Log.d(TAG, "Novo access token: " + refreshResponse.getAccessToken());
                    Log.d(TAG, "ExpiresIn: " + refreshResponse.getExpiresIn());

                    long expiresIn = refreshResponse.getExpiresIn();
                    if (expiresIn <= 0) {
                        Log.w(TAG, "expiresIn inválido (" + expiresIn + "), usando valor padrão de 3600 segundos (1 hora)");
                        expiresIn = 3600;
                    }

                    if (context != null) {
                        Log.d(TAG, "Contexto disponível, salvando novo token...");
                        // Salva novo access token e substitui refresh token pelo novo retornado (single use)
                        String newRefresh = refreshResponse.getRefreshToken() != null ? refreshResponse.getRefreshToken() : getRefreshToken();
                        saveAuthToken(refreshResponse.getAccessToken(), expiresIn, newRefresh);
                    } else {
                        Log.e(TAG, "Contexto é null, não foi possível salvar o token");
                    }

                    // Criar ApiResponse para manter compatibilidade
                    ApiResponse<RefreshTokenResponse> apiResponse = new ApiResponse<>();
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Token renovado com sucesso");
                    apiResponse.setData(refreshResponse);

                    result.setValue(apiResponse);
                    Log.i(TAG, "Refresh token bem-sucedido");
                } else {
                    // Erro na resposta da API
                    String errorMessage = "Erro desconhecido no refresh token";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = "Erro " + response.code() + ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody", e);
                            errorMessage = "Erro " + response.code() + " ao renovar token";
                        }
                    } else if (response.message() != null && !response.message().isEmpty()) {
                        errorMessage = "Erro " + response.code() + ": " + response.message();
                    }

                    ApiResponse<RefreshTokenResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage(errorMessage);
                    result.setValue(errorResponse);
                    Log.w(TAG, "Erro no refresh token: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
                // Falha na comunicação com a API
                ApiResponse<RefreshTokenResponse> failureResponse = new ApiResponse<>();
                failureResponse.setSuccess(false);
                failureResponse.setMessage("Falha na comunicação com o servidor: " + t.getMessage());
                result.setValue(failureResponse);
                Log.e(TAG, "Falha na chamada de refresh token", t);
            }
        });

        return result;
    }

    /**
     * Verifica se o usuário está autenticado e se o token não expirou.
     * Se o access token expirou mas há um refresh token válido, tenta renovar automaticamente.
     *
     * @return true se o usuário estiver autenticado e o token for válido
     */
    public boolean isAuthenticated() {
        if (encryptedPrefs == null) return false;

        String token = encryptedPrefs.getString(KEY_TOKEN, null);
        if (token == null) return false;

        long lastUsed = encryptedPrefs.getLong(KEY_LAST_USED, 0);
        long expiryTime = encryptedPrefs.getLong(KEY_EXPIRY, 0);
        long currentTime = System.currentTimeMillis();

        // Verifica se o token expirou por inatividade (3 dias sem uso)
        boolean expiredByInactivity = (currentTime - lastUsed) > THREE_DAYS_MS;

        // Verifica se o token expirou pelo tempo definido pelo servidor
        boolean expiredByServer = currentTime > expiryTime;

        if (expiredByInactivity) {
            // Token expirado por inatividade, limpa os dados de autenticação
            Log.d(TAG, "Token expirado por inatividade, realizando logout automático");
            logoutLocal();
            return false;
        }

        if (expiredByServer) {
            // Access token expirado, verifica se há refresh token válido
            if (hasValidRefreshToken()) {
                Log.d(TAG, "Access token expirado, mas há refresh token válido. Tentando renovar...");
                // Tenta renovar o token automaticamente (síncrono para esta verificação)
                refreshTokenSync();
                // Verifica novamente se agora está autenticado
                token = encryptedPrefs.getString(KEY_TOKEN, null);
                if (token != null) {
                    expiryTime = encryptedPrefs.getLong(KEY_EXPIRY, 0);
                    if (currentTime < expiryTime) {
                        return true;
                    }
                }
            }

            // Se não conseguiu renovar, faz logout
            Log.d(TAG, "Não foi possível renovar o token, realizando logout automático");
            logoutLocal();
            return false;
        }

        return true;
    }

    /**
     * Renovação síncrona do token (usado internamente).
     */
    private void refreshTokenSync() {
        String refreshToken = getRefreshToken();
        if (refreshToken == null || !hasValidRefreshToken()) {
            return;
        }

        try {
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
            Response<RefreshTokenResponse> response = authApiService.refreshToken(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                RefreshTokenResponse refreshResponse = response.body();
                long expiresIn = refreshResponse.getExpiresIn();
                if (expiresIn <= 0) {
                    expiresIn = 3600;
                }

                if (context != null) {
                    String newRefresh = refreshResponse.getRefreshToken() != null ? refreshResponse.getRefreshToken() : getRefreshToken();
                    saveAuthToken(refreshResponse.getAccessToken(), expiresIn, newRefresh);
                    Log.d(TAG, "Token renovado automaticamente");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao renovar token automaticamente", e);
        }
    }

    /**
     * Retorna o ID do usuário logado (síncrono, use apenas em background thread!)
     */
    public int getCurrentUserId() {
        if (appDatabase == null) {
            Log.e(TAG, "Banco de dados não inicializado");
            return -1;
        }
        try {
            Usuario usuario = appDatabase.usuarioDao().getFirstUser();
            if (usuario != null) {
                Log.d(TAG, "getCurrentUserId(): " + usuario.getId());
                return usuario.getId();
            } else {
                Log.d(TAG, "getCurrentUserId(): nenhum usuário encontrado");
                return -1;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar usuário no banco", e);
            return -1;
        }
    }

    /**
     * Retorna o ID do usuário logado de forma assíncrona (seguro para UI thread)
     */
    public void getCurrentUserIdAsync(java.util.function.Consumer<Integer> callback) {
        if (appDatabase == null) {
            Log.e(TAG, "Banco de dados não inicializado");
            callback.accept(-1);
            return;
        }
        executorService.execute(() -> {
            int userId = -1;
            try {
                Usuario usuario = appDatabase.usuarioDao().getFirstUser();
                if (usuario != null) {
                    userId = usuario.getId();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar usuário no banco", e);
            }
            int finalUserId = userId;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.accept(finalUserId));
        });
    }

    /**
     * Realiza o logout, removendo o token e dados relacionados.
     * Remove os dados do usuário do banco local.
     */
    public void logout() {
        logoutLocal();
    }

    /**
     * Realiza o logout local (sem chamar o servidor).
     */
    private void logoutLocal() {
        if (encryptedPrefs == null) return;

        // Limpar dados de autenticação
        encryptedPrefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_LAST_USED)
                .remove(KEY_EXPIRY)
                .remove(KEY_REFRESH_EXPIRY)
                .apply();

        // Limpar dados do usuário do Room
        if (appDatabase != null) {
            executorService.execute(() -> {
                try {
                    appDatabase.usuarioDao().deleteAllUsers();
                    Log.d(TAG, "Dados do usuário removidos do Room");
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao remover dados do usuário do Room", e);
                }
            });
        }

        Log.d(TAG, "Logout local realizado com sucesso");
    }

    /**
     * Realiza o logout chamando o endpoint do servidor e depois limpa os dados locais.
     *
     * @return LiveData contendo a resposta da API
     */
    public LiveData<ApiResponse<Void>> logoutWithServer() {
        MutableLiveData<ApiResponse<Void>> result = new MutableLiveData<>();

        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            // Se não há refresh token, apenas faz logout local
            logoutLocal();
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Logout realizado com sucesso");
            result.setValue(response);
            return result;
        }

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        authApiService.logout(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Independente da resposta do servidor, sempre faz logout local
                logoutLocal();

                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    Log.i(TAG, "Logout no servidor realizado com sucesso");
                } else {
                    // Mesmo com erro no servidor, considera sucesso pois limpou localmente
                    ApiResponse<Void> successResponse = new ApiResponse<>();
                    successResponse.setSuccess(true);
                    successResponse.setMessage("Logout realizado com sucesso");
                    result.setValue(successResponse);
                    Log.w(TAG, "Erro no servidor durante logout, mas logout local realizado");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Mesmo com falha na comunicação, sempre faz logout local
                logoutLocal();

                ApiResponse<Void> successResponse = new ApiResponse<>();
                successResponse.setSuccess(true);
                successResponse.setMessage("Logout realizado com sucesso");
                result.setValue(successResponse);
                Log.w(TAG, "Falha na comunicação durante logout, mas logout local realizado", t);
            }
        });

        return result;
    }

    public LiveData<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        MutableLiveData<RefreshTokenResponse> result = new MutableLiveData<>();

        authApiService.refreshToken(request).enqueue(new Callback<RefreshTokenResponse>() {
            @Override
            public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    Log.e(TAG, "Erro ao renovar token: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao renovar token", t);
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<UserProfileResponse> getUserProfile() {
        MutableLiveData<UserProfileResponse> result = new MutableLiveData<>();

        // Usar AuthApiService autenticado para incluir o token no header
        AuthApiService authenticatedAuthService = RetrofitClient.getAuthApiService(context);

        authenticatedAuthService.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    Log.d(TAG, "Perfil do usuário carregado: " + profile.getName());
                    result.postValue(profile);
                } else {
                    Log.e(TAG, "Erro ao buscar perfil do usuário: " + response.code());
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao buscar perfil do usuário", t);
                result.postValue(null);
            }
        });

        return result;
    }
}