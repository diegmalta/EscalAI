package com.ufrj.escalaiv2.dao;

import android.util.Log;

import com.ufrj.escalaiv2.conexao.Conexao;
import com.ufrj.escalaiv2.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
