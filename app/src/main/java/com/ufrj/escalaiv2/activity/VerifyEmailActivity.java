package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.viewmodel.VerifyEmailVM;
import android.widget.TextView;

public class VerifyEmailActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private TextInputEditText emailInput, codeInput;
    private MaterialButton verifyButton;
    private ProgressBar progressBar;
    private MaterialCardView errorContainer, successContainer;
    private TextView errorText, successText;
    private VerifyEmailVM viewModel;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        // Obter email passado pela intent
        userEmail = getIntent().getStringExtra(EXTRA_EMAIL);

        // Inicializar views
        initializeViews();

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(VerifyEmailVM.class);

        // Configurar observers
        setupObservers();

        // Configurar listeners
        setupListeners();

        // Preencher campo de email
        if (userEmail != null && !userEmail.isEmpty()) {
            emailInput.setText(userEmail);
        }
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        codeInput = findViewById(R.id.codeInput);
        verifyButton = findViewById(R.id.verifyButton);
        progressBar = findViewById(R.id.progressBar);
        errorContainer = findViewById(R.id.errorContainer);
        successContainer = findViewById(R.id.successContainer);
        errorText = findViewById(R.id.errorText);
        successText = findViewById(R.id.successText);
    }

    private void setupObservers() {
        // Observar estado de carregamento
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            verifyButton.setEnabled(!isLoading);
            codeInput.setEnabled(!isLoading);
        });

        // Observar mensagens de erro
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                errorText.setText(errorMsg);
                errorContainer.setVisibility(View.VISIBLE);
                successContainer.setVisibility(View.GONE);
            } else {
                errorContainer.setVisibility(View.GONE);
            }
        });

        // Observar mensagens de sucesso
        viewModel.getSuccessMessage().observe(this, successMsg -> {
            if (successMsg != null && !successMsg.isEmpty()) {
                successText.setText(successMsg);
                successContainer.setVisibility(View.VISIBLE);
                errorContainer.setVisibility(View.GONE);
            }
        });

        // Observar sucesso de verificação
        viewModel.getVerificationSuccess().observe(this, success -> {
            if (success) {
                // Aguardar 2 segundos e voltar para tela de login
                verifyButton.postDelayed(() -> {
                    Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }, 2000);
            }
        });
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> verifyEmail());

        codeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                verifyEmail();
                return true;
            }
            return false;
        });
    }

    private void verifyEmail() {
        String email = emailInput.getText().toString().trim();
        String code = codeInput.getText().toString().trim().toUpperCase();

        // Limpar mensagens anteriores
        errorContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);

        // Validação básica
        if (email.isEmpty()) {
            errorText.setText("Por favor, insira seu email");
            errorContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (code.isEmpty()) {
            errorText.setText("Por favor, insira o código de verificação");
            errorContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (code.length() != 6) {
            errorText.setText("O código deve ter 6 caracteres");
            errorContainer.setVisibility(View.VISIBLE);
            return;
        }

        // Esconder teclado
        hideKeyboard();

        // Chamar ViewModel para verificar
        viewModel.verifyEmail(email, code);
    }

    private void hideKeyboard() {
        android.view.inputmethod.InputMethodManager imm = 
            (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}



