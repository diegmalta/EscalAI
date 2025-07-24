package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.ufrj.escalaiv2.viewmodel.LoginVM;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button button_login, button_cadastro, recuperarSenhaButton;
    private TextView Resultado;
    private ProgressBar progressBar;
    private LoginVM viewModel;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurações para evitar problemas com IME
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(LoginVM.class);

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
        progressBar = findViewById(R.id.progressBar);

        // Configurações adicionais para os EditText
        setupEditTextConfigurations();
    }

    private void setupEditTextConfigurations() {
        // Configurações para evitar problemas com IME
        editEmail.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_NEXT);
        editPassword.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);

        // Configurar navegação entre campos
        editEmail.setNextFocusDownId(R.id.editPassword);
        editPassword.setNextFocusDownId(R.id.button_login);

        // Configurar listeners para melhor controle do IME
        editPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                login();
                return true;
            }
            return false;
        });
    }

    private void setupObservers() {
        // Observa o estado de carregamento
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            button_login.setEnabled(!isLoading);
            button_cadastro.setEnabled(!isLoading);
            recuperarSenhaButton.setEnabled(!isLoading);
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
        button_login.setOnClickListener(v -> {
            hideKeyboard();
            login();
        });

        button_cadastro.setOnClickListener(v -> {
            hideKeyboard();
            cadastrar();
        });

        recuperarSenhaButton.setOnClickListener(v -> {
            hideKeyboard();
            // Implementação futura para recuperação de senha
            Toast.makeText(MainActivity.this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
    }

    private void hideKeyboard() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String senha = editPassword.getText().toString();

        // Limpa mensagens anteriores
        Resultado.setText("");
        Resultado.setVisibility(View.GONE);

        // Define estado de carregamento
        viewModel.setLoading(true);

        // Chama o ViewModel para fazer login via API
        viewModel.login(email, senha).observe(this, response -> {
            viewModel.setLoading(false);
            handleLoginResponse(response);
        });
    }

    private void handleLoginResponse(ApiResponse<LoginResponse> response) {
        if (response != null && response.isSuccess()) {
            Resultado.setText("Login efetuado com sucesso");
            Resultado.setVisibility(View.VISIBLE);
            // Pequeno delay para mostrar a mensagem antes de navegar
            editEmail.postDelayed(this::navigateToMainScreen, 500);
        } else {
            String errorMsg = response != null && response.getMessage() != null ?
                    response.getMessage() : "Erro ao fazer login";
            Resultado.setText(errorMsg);
            Resultado.setVisibility(View.VISIBLE);
            viewModel.setErrorMessage(errorMsg);
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

    @Override
    protected void onPause() {
        super.onPause();
        // Esconde o teclado quando a activity é pausada
        hideKeyboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpa referências para evitar memory leaks
        viewModel = null;
    }
}
