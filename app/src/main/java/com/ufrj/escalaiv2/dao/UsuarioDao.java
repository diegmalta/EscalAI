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

    @Update
    void update(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<Usuario> getUsuario(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    LiveData<Usuario> getUsuarioPorEmail(String email);

    @Query("SELECT * FROM users")
    LiveData<List<Usuario>> getAllUsuarios();

    @Query("SELECT * FROM users WHERE email = :email AND password = :senha")
    Usuario selecionaUsuario(String email, String senha);
}
/*
public class UsuarioDAO {

    public Usuario selecionaUsuario(String email, String senha) {
        try {
            Connection conn = Conexao.conectar();
            if (conn != null) {
                String sql = "select * from usuarios where email = '" + email + "' and senha = '" + senha + "'";
                Statement st = null;
                st = conn.createStatement();

                ResultSet rs = st.executeQuery(sql);
                if (rs.next()) {
                    Usuario usu = new Usuario();
                    usu.setCodigo(rs.getInt(1));
                    usu.setEmail(rs.getString(2));
                    usu.setSenha(rs.getString(3));

                    conn.close();
                    return usu;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("Erro", e.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e("Erro", throwables.getMessage());
        }

        return null;
    }

    public void cadastrarUsuario(Usuario u) throws SQLException, ClassNotFoundException {
        Connection conn = Conexao.conectar();
        if (conn != null) {
            String sql = "insert into usuarios (email, nome, dataNasc, celular, senha) values ('" + u.getEmail() + "','" + u.getNome() + "'," +
                    "'" + u.getDataNasc() + "','" + u.getCelular() + "','" + u.getSenha() + "')";
            Statement st = conn.createStatement();
            st.executeQuery(sql);
            conn.close();
        }
    }

}
*/