package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.model.AppDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button button_login, button_cadastro;
    TextView Resultado;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        button_login = findViewById(R.id.button_login);
        button_cadastro = findViewById(R.id.button_cadastro);
        Resultado = findViewById(R.id.Resultado);
    }

    public void login(View v) {
        String usuario = editEmail.getText().toString();
        String senha = editPassword.getText().toString();

        if(usuario.equals("teste") && senha.equals("teste")){
            Resultado.setText("Login efetuado com sucesso");
            Intent intent = new Intent(MainActivity.this, getUserInfoForm());
            startActivity(intent);
            finish();
        } else {
            // Execute database query in background
            new Thread(() -> {
                AppDatabase database = AppDatabase.getInstance(MainActivity.this);
                Usuario usu = database.usuarioDao().selecionaUsuario(usuario, senha);

                // Update UI on main thread
                runOnUiThread(() -> {
                    if (usu != null) {
                        Resultado.setText("Login efetuado com sucesso");
                        Intent intent = new Intent(MainActivity.this, getMenuPrincipal());
                        startActivity(intent);
                        finish();
                    } else {
                        Resultado.setText("Usuário ou senha inválidos");
                        limpar();
                    }
                });
            }).start();
        }
    }

    @NonNull
    private static Class<MenuPrincipalActivity> getMenuPrincipal() {
        return MenuPrincipalActivity.class;
    }

    @NonNull
    private static Class<FormInfoUsuariosActivity> getUserInfoForm() {
        return FormInfoUsuariosActivity.class;
    }

    public void cadastrar(View view) {
        Intent intent = new Intent(this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

    private void limpar() {
        //editEmail.setText("");
        editPassword.setText("");
        editEmail.requestFocus();
    }

}
