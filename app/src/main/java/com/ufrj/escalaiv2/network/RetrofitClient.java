package com.ufrj.escalaiv2.network;

import android.content.Context;
import com.ufrj.escalaiv2.utils.ConfigHelper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton para fornecer a instância do cliente Retrofit.
 */
public class RetrofitClient {

    private static Retrofit retrofitInstance = null;
    private static Retrofit authenticatedRetrofitInstance = null;
    private static AuthApiService authApiServiceInstance = null;
    private static LesaoApiService lesaoApiServiceInstance = null;
    private static AtividadesApiService atividadesApiServiceInstance = null;
    private static ExerciciosApiService exerciciosApiServiceInstance = null;

    private static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            
            // Configurar logging apenas se habilitado no BuildConfig
            if (ConfigHelper.isLoggingEnabled()) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }
            
            // Adicionar header do ngrok apenas se configurado
            if (ConfigHelper.shouldUseNgrokHeader()) {
                httpClient.addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                        .header("ngrok-skip-browser-warning", "true");
                    return chain.proceed(requestBuilder.build());
                });
            }
            
            httpClient.connectTimeout(10, TimeUnit.SECONDS);
            httpClient.readTimeout(10, TimeUnit.SECONDS);
            httpClient.writeTimeout(10, TimeUnit.SECONDS);

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(ConfigHelper.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofitInstance;
    }

    private static Retrofit getAuthenticatedRetrofitInstance(Context context) {
        if (authenticatedRetrofitInstance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            
            // Configurar logging apenas se habilitado no BuildConfig
            if (ConfigHelper.isLoggingEnabled()) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }
            
            // Adicionar header do ngrok apenas se configurado
            if (ConfigHelper.shouldUseNgrokHeader()) {
                httpClient.addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                        .header("ngrok-skip-browser-warning", "true");
                    return chain.proceed(requestBuilder.build());
                });
            }
            
            httpClient.addInterceptor(new AuthInterceptor(context));
            httpClient.connectTimeout(10, TimeUnit.SECONDS);
            httpClient.readTimeout(10, TimeUnit.SECONDS);
            httpClient.writeTimeout(10, TimeUnit.SECONDS);

            authenticatedRetrofitInstance = new Retrofit.Builder()
                    .baseUrl(ConfigHelper.getBaseUrl())
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
     * Obtém a instância do serviço da API de autenticação com autenticação automática.
     *
     * @param context Contexto da aplicação
     * @return Instância de AuthApiService autenticada
     */
    public static AuthApiService getAuthApiService(Context context) {
        return getAuthenticatedRetrofitInstance(context).create(AuthApiService.class);
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

    /**
     * Obtém a instância singleton do serviço da API de exercícios.
     *
     * @return Instância de ExerciciosApiService
     */
    public static ExerciciosApiService getExerciciosApiService() {
        if (exerciciosApiServiceInstance == null) {
            exerciciosApiServiceInstance = getRetrofitInstance().create(ExerciciosApiService.class);
        }
        return exerciciosApiServiceInstance;
    }

    /**
     * Obtém a instância singleton do serviço da API de exercícios com autenticação automática.
     *
     * @param context Contexto da aplicação
     * @return Instância de ExerciciosApiService
     */
    public static ExerciciosApiService getExerciciosApiService(Context context) {
        return getAuthenticatedRetrofitInstance(context).create(ExerciciosApiService.class);
    }

    /**
     * Reseta todas as instâncias singleton.
     * Útil quando a BASE_URL é alterada durante o desenvolvimento.
     * Chame este método antes de usar os serviços após mudar a URL.
     */
    public static void resetInstances() {
        retrofitInstance = null;
        authenticatedRetrofitInstance = null;
        authApiServiceInstance = null;
        lesaoApiServiceInstance = null;
        atividadesApiServiceInstance = null;
        exerciciosApiServiceInstance = null;
    }
}
