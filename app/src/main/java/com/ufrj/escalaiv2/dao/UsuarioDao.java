package com.ufrj.escalaiv2.dao;

import com.ufrj.escalaiv2.model.Usuario;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsuarioDao {
    @Insert
    long insert(Usuario usuario);

    @Insert
    long insertUser(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Update
    void updateUser(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<Usuario> getUsuario(int id);

    @Query("SELECT * FROM users WHERE id = :id")
    Usuario getUserById(int id);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<Usuario> getUserByIdLiveData(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    LiveData<Usuario> getUsuarioPorEmail(String email);

    @Query("SELECT * FROM users")
    LiveData<List<Usuario>> getAllUsuarios();

    @Query("SELECT * FROM users WHERE email = :email AND password = :senha")
    Usuario selecionaUsuario(String email, String senha);

    @Query("SELECT name FROM users WHERE id = :userId LIMIT 1")
    String getUserNameById(int userId);
}
