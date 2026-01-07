package com.ufrj.escalaiv2.utils;

import com.ufrj.escalaiv2.BuildConfig;

/**
 * Classe auxiliar para acessar configurações do BuildConfig de forma segura.
 * As configurações são lidas do local.properties durante o build.
 */
public class ConfigHelper {
    
    /**
     * Obtém a URL base da API.
     * @return URL base da API
     */
    public static String getBaseUrl() {
        return BuildConfig.BASE_URL;
    }
    
    /**
     * Verifica se o logging está habilitado.
     * @return true se logging está habilitado
     */
    public static boolean isLoggingEnabled() {
        return BuildConfig.ENABLE_LOGGING;
    }
    
    /**
     * Verifica se deve usar o header do ngrok.
     * @return true se deve usar header ngrok
     */
    public static boolean shouldUseNgrokHeader() {
        return BuildConfig.USE_NGROK_HEADER;
    }
}
