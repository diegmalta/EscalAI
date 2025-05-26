package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TreinoVMFactory implements ViewModelProvider.Factory {
    private final Application application;

    public TreinoVMFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TreinoVM.class)) {
            return (T) new TreinoVM(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}