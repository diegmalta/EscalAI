package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HumorVMFactory implements ViewModelProvider.Factory {
    private final Application application;

    public HumorVMFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HumorVM.class)) {
            return (T) new HumorVM(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}