package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DorVMFactory implements ViewModelProvider.Factory {
    private final Application application;

    public DorVMFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DorVM.class)) {
            return (T) new DorVM(application);
        }
        throw new IllegalArgumentException("ViewModel desconhecido");
    }
}