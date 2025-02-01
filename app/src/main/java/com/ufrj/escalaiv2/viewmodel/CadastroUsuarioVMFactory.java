package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class CadastroUsuarioVMFactory implements ViewModelProvider.Factory {
    private Application application;

    public CadastroUsuarioVMFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CadastroUsuarioVM.class)) {
            return (T) new CadastroUsuarioVM(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}