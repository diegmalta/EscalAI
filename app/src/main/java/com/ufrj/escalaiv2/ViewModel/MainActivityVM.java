package com.ufrj.escalaiv2.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.escalaiv2.R;
import com.ufrj.escalaiv2.Model.Usuario;
import com.ufrj.escalaiv2.dao.UsuarioDAO;

public class MainActivityVM extends AppCompatActivity {

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
            Intent intent = new Intent(MainActivityVM.this, getUserInfoForm());
            startActivity(intent);
        }

        Usuario usu = new UsuarioDAO().selecionaUsuario(usuario, senha);
        if (usu != null) {
            Resultado.setText("Login efetuado com sucesso");
            Intent intent = new Intent(MainActivityVM.this, getMenuPrincipal());
            startActivity(intent);
            finish();
        } else {
            Resultado.setText("Usuário ou senha inválidos");
            limpar();
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

    public void cadastrar(View v) {
        Intent intent = new Intent(MainActivityVM.this, CadastroUsuarioVM.class);
        startActivity(intent);
    }

    private void limpar() {
        //editEmail.setText("");
        editPassword.setText("");
        editEmail.requestFocus();
    }

}
