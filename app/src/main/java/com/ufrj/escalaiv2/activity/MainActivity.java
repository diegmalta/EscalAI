package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.dto.LoginResponse;
import com.ufrj.escalaiv2.viewmodel.LoginViewModel;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button button_login, button_cadastro, recuperarSenhaButton;
    private TextView Resultado;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Verifica se já está autenticado
        if (viewModel.isAuthenticated()) {
            navigateToMainScreen();
            return;
        }

        // Inicializa as views
        initializeViews();
        setupObservers();
        setupListeners();
    }

    private void initializeViews() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        button_login = findViewById(R.id.button_login);
        button_cadastro = findViewById(R.id.button_cadastro);
        recuperarSenhaButton = findViewById(R.id.recuperarSenhaButton);
        Resultado = findViewById(R.id.Resultado);

        // Adicione um ProgressBar ao seu layout e inicialize aqui
        // progressBar = findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        // Observa o estado de carregamento
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            button_login.setEnabled(!isLoading);
        });

        // Observa mensagens de erro
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Resultado.setText(errorMsg);
                Resultado.setVisibility(View.VISIBLE);
            } else {
                Resultado.setVisibility(View.GONE);
            }
        });
    }

    private void setupListeners() {
        button_login.setOnClickListener(v -> login());

        button_cadastro.setOnClickListener(v -> cadastrar());

        recuperarSenhaButton.setOnClickListener(v -> {
            // Implementação futura para recuperação de senha
            Toast.makeText(MainActivity.this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String senha = editPassword.getText().toString();

        // Limpa mensagens anteriores
        Resultado.setText("");
        Resultado.setVisibility(View.GONE);

        // Chama o ViewModel para fazer login via API
        viewModel.login(email, senha).observe(this, this::handleLoginResponse);
    }

    private void handleLoginResponse(ApiResponse<LoginResponse> response) {
        if (response.isSuccess()) {
            Resultado.setText("Login efetuado com sucesso");
            Resultado.setVisibility(View.VISIBLE);
            navigateToMainScreen();
        } else {
            Resultado.setText(response.getMessage() != null ?
                    response.getMessage() : "Erro ao fazer login");
            Resultado.setVisibility(View.VISIBLE);
            limpar();
        }
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(MainActivity.this, getMenuPrincipal());
        startActivity(intent);
        finish();
    }

    @NonNull
    private static Class<MenuPrincipalActivity> getMenuPrincipal() {
        return MenuPrincipalActivity.class;
    }

    @NonNull
    private static Class<FormInfoUsuariosActivity> getUserInfoForm() {
        return FormInfoUsuariosActivity.class;
    }

    public void cadastrar() {
        Intent intent = new Intent(this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

    private void limpar() {
        editPassword.setText("");
        editEmail.requestFocus();
    }
}
