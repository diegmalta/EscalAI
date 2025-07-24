package com.ufrj.escalaiv2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Classe Application personalizada para configurar comportamentos globais.
 */
public class EscalAIApplication extends Application {

    private static final String TAG = "EscalAIApplication";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // Configurações globais para melhorar a estabilidade
        setupGlobalConfigurations();
    }

    private void setupGlobalConfigurations() {
        try {
            // Configurações para evitar problemas com threads e sessões
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                Log.e(TAG, "Uncaught exception in thread " + thread.getName(), throwable);

                // Se for um erro relacionado a sessões ou threads, apenas loga sem crashar
                if (throwable.getMessage() != null &&
                    (throwable.getMessage().contains("Session id mismatch") ||
                     throwable.getMessage().contains("RemoteInputConnectionImpl") ||
                     throwable.getMessage().contains("ImeBackAnimationController"))) {
                    Log.w(TAG, "Session-related error caught and handled: " + throwable.getMessage());
                    return;
                }

                // Para outros erros, permite o comportamento padrão
                throwable.printStackTrace();
            });

            Log.d(TAG, "Global configurations initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up global configurations", e);
        }
    }

    public static Context getAppContext() {
        return context;
    }
}