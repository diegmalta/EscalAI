package com.ufrj.escalaiv2.conexao;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // Metodo de conexao com o banco de dados
    public static Connection conectar() throws ClassNotFoundException, SQLException {
        // Objeto de conexao
        Connection conn = null;

        // Adicionar politica para criacao de thread
        StrictMode.ThreadPolicy politica;
        politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);

        // Verificar se Driver de conexao esta importado no projeto
        Class.forName("net.sourceforge.jtds.jdbc.Driver");

        String ip = "192.168.0.14:1433";
        String db = "EscalAI";
        String user = "sa";
        String senha = "123";

        String connectionString = "jdbc:jtds:sqlserver://" + ip + ";databaseName=" + db +
                ";user=" + user + ";password=" + senha + ";";
        conn = DriverManager.getConnection(connectionString);

        return conn;
    }
}
