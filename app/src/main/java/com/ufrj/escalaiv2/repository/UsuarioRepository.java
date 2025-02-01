package com.ufrj.escalaiv2.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.dao.UsuarioDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {
    private UsuarioDao usuarioDao;
    private LiveData<List<Usuario>> allUsuarios;
    private ExecutorService executorService;

    public UsuarioRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        usuarioDao = database.usuarioDao();
        allUsuarios = usuarioDao.getAllUsuarios();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Usuario usuario) {
        executorService.execute(() -> usuarioDao.insert(usuario));
    }

    public void update(Usuario usuario) {
        executorService.execute(() -> usuarioDao.update(usuario));
    }

    public void delete(Usuario usuario) {
        executorService.execute(() -> usuarioDao.delete(usuario));
    }

    public LiveData<Usuario> getUsuario(int codigo) {
        return usuarioDao.getUsuario(codigo);
    }

    public LiveData<Usuario> getUsuarioPorEmail(String email) {
        return usuarioDao.getUsuarioPorEmail(email);
    }

    public LiveData<List<Usuario>> getAllUsuarios() {
        return allUsuarios;
    }
}