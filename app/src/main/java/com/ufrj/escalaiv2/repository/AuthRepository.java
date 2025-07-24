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
    private static final String KEY_LAST_USED = "last_used_timestamp";
    private static final String KEY_EXPIRY = "token_expiry";
    private static final String KEY_USER_ID = "user_id";
    private static final long THREE_DAYS_MS = 3 * 24 * 60 * 60 * 1000L; // 3 dias em milissegundos

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
                        saveAuthToken(loginResponse.getToken(), expiresIn);
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
                    String errorMessage = "Erro desconhecido no login";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = "Erro " + response.code() + ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody", e);
                            errorMessage = "Erro " + response.code() + " ao fazer login";
                        }
                    } else if (response.message() != null && !response.message().isEmpty()){
                        errorMessage = "Erro " + response.code() + ": " + response.message();
                    }

                    ApiResponse<LoginResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage(errorMessage);
                    result.setValue(errorResponse);
                    Log.w(TAG, "Erro no login: " + errorMessage);
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
     * Salva o token de autenticação de forma segura.
     *
     * @param token Token JWT recebido da API
     * @param expiresIn Tempo de expiração do token em segundos
     */
    private void saveAuthToken(String token, long expiresIn) {
        if (encryptedPrefs == null) {
            Log.e(TAG, "EncryptedSharedPreferences não inicializado");
            return;
        }

        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + (expiresIn * 1000); // Converte segundos para milissegundos

        Log.d(TAG, "Salvando token. expiresIn: " + expiresIn + " (segundos), expiryTime: " + expiryTime + " (ms), currentTime: " + currentTime);

        encryptedPrefs.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_LAST_USED, currentTime)
                .putLong(KEY_EXPIRY, expiryTime)
                .apply();

        Log.d(TAG, "Token salvo com sucesso. Expira em: " + new Date(expiryTime));
        Log.d(TAG, "Token salvo: " + token);
        Log.d(TAG, "Expira em: " + new Date(expiryTime));

        // Verificar se foi salvo corretamente
        String savedToken = encryptedPrefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Token verificado após salvar: " + savedToken);
        Log.d(TAG, "=== FIM SAVE AUTH TOKEN DEBUG ===");
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
     * Verifica se o usuário está autenticado e se o token não expirou.
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

        if (expiredByInactivity || expiredByServer) {
            // Token expirado, limpa os dados de autenticação
            Log.d(TAG, "Token expirado, realizando logout automático");
            logout();
            return false;
        }

        return true;
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
        if (encryptedPrefs == null) return;

        // Limpar dados de autenticação
        encryptedPrefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_LAST_USED)
                .remove(KEY_EXPIRY)
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

        Log.d(TAG, "Logout realizado com sucesso");
    }
}