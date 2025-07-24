package com.ufrj.escalaiv2.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Utilitário para gerenciar o Input Method Editor (IME) de forma segura
 * e evitar problemas de sessão e crashes relacionados ao teclado virtual.
 */
public class InputMethodUtils {

    /**
     * Esconde o teclado virtual de forma segura
     * @param activity A activity atual
     */
    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            // Log do erro mas não crasha a aplicação
            e.printStackTrace();
        }
    }

    /**
     * Mostra o teclado virtual de forma segura
     * @param activity A activity atual
     * @param editText O EditText que deve receber o foco
     */
    public static void showKeyboard(Activity activity, EditText editText) {
        if (activity == null || editText == null) return;
        
        try {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            // Log do erro mas não crasha a aplicação
            e.printStackTrace();
        }
    }

    /**
     * Configura um EditText para navegação segura entre campos
     * @param editText O EditText a ser configurado
     * @param nextFocusId ID do próximo campo a receber foco
     */
    public static void setupEditTextNavigation(EditText editText, int nextFocusId) {
        if (editText == null) return;
        
        try {
            editText.setNextFocusDownId(nextFocusId);
            editText.setSingleLine(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpa o foco de todos os EditTexts de forma segura
     * @param activity A activity atual
     */
    public static void clearFocus(Activity activity) {
        if (activity == null) return;
        
        try {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 