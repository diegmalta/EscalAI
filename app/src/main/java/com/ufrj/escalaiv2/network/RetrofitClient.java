package com.ufrj.escalaiv2.network;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton para fornecer a instância do cliente Retrofit.
 */
public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.0.18:8000/";

    private static Retrofit retrofitInstance = null;
    private static Retrofit authenticatedRetrofitInstance = null;
    private static AuthApiService authApiServiceInstance = null;
    private static LesaoApiService lesaoApiServiceInstance = null;
    private static AtividadesApiService atividadesApiServiceInstance = null;

    private static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Defina o nível de log: NONE, BASIC, HEADERS, BODY
            // Use BODY apenas em debug, pois loga dados sensíveis!
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // TODO Mude para NONE em produção

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.connectTimeout(10, TimeUnit.SECONDS); // Timeout de conexão
            httpClient.readTimeout(10, TimeUnit.SECONDS);    // Timeout de leitura
            httpClient.writeTimeout(10, TimeUnit.SECONDS);   // Timeout de escrita

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use o conversor apropriado
                    .client(httpClient.build()) // Define o OkHttpClient customizado
                    .build();
        }
        return retrofitInstance;
    }

    private static Retrofit getAuthenticatedRetrofitInstance(Context context) {
        if (authenticatedRetrofitInstance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // TODO Mude para NONE em produção

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(new AuthInterceptor(context));
            httpClient.connectTimeout(10, TimeUnit.SECONDS);
            httpClient.readTimeout(10, TimeUnit.SECONDS);
            httpClient.writeTimeout(10, TimeUnit.SECONDS);

            authenticatedRetrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return authenticatedRetrofitInstance;
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

    /**
     * Obtém a instância singleton do serviço da API de lesões com autenticação automática.
     *
     * @param context Contexto da aplicação
     * @return Instância de LesaoApiService
     */
    public static LesaoApiService getLesaoApiService(Context context) {
        return getAuthenticatedRetrofitInstance(context).create(LesaoApiService.class);
    }

    /**
     * Obtém a instância singleton do serviço da API de atividades.
     *
     * @return Instância de AtividadesApiService
     */
    public static AtividadesApiService getAtividadesApiService() {
        if (atividadesApiServiceInstance == null) {
            atividadesApiServiceInstance = getRetrofitInstance().create(AtividadesApiService.class);
        }
        return atividadesApiServiceInstance;
    }

    /**
     * Obtém a instância singleton do serviço da API de atividades com autenticação automática.
     *
     * @param context Contexto da aplicação
     * @return Instância de AtividadesApiService
     */
    public static AtividadesApiService getAtividadesApiService(Context context) {
        return getAuthenticatedRetrofitInstance(context).create(AtividadesApiService.class);
    }
}
