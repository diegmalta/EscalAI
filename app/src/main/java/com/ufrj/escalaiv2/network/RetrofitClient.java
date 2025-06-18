package com.ufrj.escalaiv2.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton para fornecer a instância do cliente Retrofit.
 */
public class RetrofitClient {

    private static final String BASE_URL = "https://escalai.free.beeceptor.com/";

    private static Retrofit retrofitInstance = null;
    private static AuthApiService authApiServiceInstance = null;
    private static LesaoApiService lesaoApiServiceInstance = null;

    private static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Defina o nível de log: NONE, BASIC, HEADERS, BODY
            // Use BODY apenas em debug, pois loga dados sensíveis!
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // TODO Mude para NONE em produção

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.connectTimeout(30, TimeUnit.SECONDS); // Timeout de conexão
            httpClient.readTimeout(30, TimeUnit.SECONDS);    // Timeout de leitura
            httpClient.writeTimeout(30, TimeUnit.SECONDS);   // Timeout de escrita

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use o conversor apropriado
                    .client(httpClient.build()) // Define o OkHttpClient customizado
                    .build();
        }
        return retrofitInstance;
    }

    /**
     * Obtém a instância singleton do serviço da API de autenticação.
     *
     * @return Instância de AuthApiService
     */
    public static AuthApiService getAuthApiService() {
        if (authApiServiceInstance == null) {
            authApiServiceInstance = getRetrofitInstance().create(AuthApiService.class);
        }
        return authApiServiceInstance;
    }

    /**
     * Obtém a instância singleton do serviço da API de lesões.
     *
     * @return Instância de LesaoApiService
     */
    public static LesaoApiService getLesaoApiService() {
        if (lesaoApiServiceInstance == null) {
            lesaoApiServiceInstance = getRetrofitInstance().create(LesaoApiService.class);
        }
        return lesaoApiServiceInstance;
    }
}
