package com.ufrj.escalaiv2.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LiveData;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.viewmodel.CadastroUsuarioVM;
import com.ufrj.escalaiv2.dto.ApiResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.graphics.Color;
import android.text.TextPaint;
import java.util.List;
import java.util.ArrayList;

import com.ufrj.escalaiv2.viewmodel.CadastroUsuarioVMFactory;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private EditText emailEdit, nomeEdit, sobrenomeEdit, dataNascimentoEdit,
            celularEdit, senhaEdit, confirmaSenhaEdit;
    private TextView errosCadastro;
    private TextView termsTextView;
    private CheckBox agreeToTermsCheckBox;
    private Button cadastrarButton;
    private ProgressBar progressBar;
    private CadastroUsuarioVM viewModel;
    private Calendar calendar;

    private void initializeViewModel() {
        CadastroUsuarioVMFactory factory = new CadastroUsuarioVMFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(CadastroUsuarioVM.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        initializeViewModel();
        initializeViews();
        setupObservers();
        setupDatePicker();
        setupTermsLink();
        setupListeners();
    }

    private void initializeViews() {
        emailEdit = findViewById(R.id.email);
        nomeEdit = findViewById(R.id.nome);
        sobrenomeEdit = findViewById(R.id.sobrenome);
        dataNascimentoEdit = findViewById(R.id.dataNascimento);
        celularEdit = findViewById(R.id.celular);
        senhaEdit = findViewById(R.id.senha);
        confirmaSenhaEdit = findViewById(R.id.confirmaSenha);
        errosCadastro = findViewById(R.id.errosCadastro);
        termsTextView = findViewById(R.id.termsTextView);
        agreeToTermsCheckBox = findViewById(R.id.agreeToTermsCheckBox);
        cadastrarButton = findViewById(R.id.cadastrarButton);
        progressBar = findViewById(R.id.progressBarCadastro);
    }

/*    private void setupTermsLink() {
        String fullText = "Leia os termos e condições de uso aqui";
        SpannableString spannableString = new SpannableString(fullText);

        int startPos = fullText.lastIndexOf("aqui");
        int endPos = startPos + "aqui".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(CadastroUsuarioActivity.this, TermsOfUseActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        readTermsLink.setText(spannableString);
        readTermsLink.setMovementMethod(LinkMovementMethod.getInstance());
    }*/

    private void setupObservers() {
        viewModel.getErros().observe(this, erros -> {
            if (erros != null && !erros.isEmpty()) {
                errosCadastro.setText(String.join("\n", erros));
                errosCadastro.setVisibility(TextView.VISIBLE);
            } else {
                errosCadastro.setVisibility(TextView.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                cadastrarButton.setEnabled(!isLoading);
            }
        });

        viewModel.getRegistrationResult().observe(this, apiResponse -> {
            // Este observer será ativado quando a ViewModel postar um novo resultado
            // (geralmente após a chamada da API retornar no repositório)
            // Mas a lógica de observação da chamada em si está no listener do botão agora.
            // Podemos usar isso para limpar a UI ou fazer algo após o resultado final.
        });

        viewModel.getCadastroSucesso().observe(this, sucesso -> {
            if (sucesso) {
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupDatePicker() {
        calendar = Calendar.getInstance();
        dataNascimentoEdit.setOnClickListener(v -> showDatePickerDialog());
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateEditText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());
        dataNascimentoEdit.setText(formattedDate);
        viewModel.setDataNascimento(formattedDate);
    }

    private void setupTermsLink() {
        String fullText = getString(R.string.termos_de_uso_label); // Ex: "Li e aceito os Termos e Condições de Uso aqui"
        String linkText = getString(R.string.termos_de_uso_link);   // Ex: "aqui"

        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf(linkText);
        int endIndex = startIndex + linkText.length();

        if (startIndex != -1) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // Abrir a TermsOfUseActivity
                    Intent intent = new Intent(CadastroUsuarioActivity.this, TermsOfUseActivity.class);
                    startActivity(intent);
                    // Adicionar animação (opcional)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            };

            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.seed)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Defina a cor em colors.xml

            termsTextView.setText(spannableString);
            termsTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Torna o link clicável
        } else {
            termsTextView.setText(fullText); // Caso não encontre o texto do link
        }
    }

    private void setupListeners() {
        cadastrarButton.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String email = emailEdit.getText().toString().trim();
        String nome = nomeEdit.getText().toString().trim();
        String sobrenome = sobrenomeEdit.getText().toString().trim();
        String dataNascimentoStr = dataNascimentoEdit.getText().toString().trim();
        //String celular = celularEdit.getText().toString().trim();
        String senha = senhaEdit.getText().toString();
        String confirmaSenha = confirmaSenhaEdit.getText().toString();
        boolean aceitouTermos = agreeToTermsCheckBox.isChecked();

        errosCadastro.setVisibility(View.GONE);
        errosCadastro.setText("");

        LiveData<ApiResponse<Void>> apiCallLiveData = viewModel.cadastrarUsuario(email, nome, sobrenome, dataNascimentoStr, senha, confirmaSenha, aceitouTermos);

        // Este LiveData será ativado apenas uma vez com o resultado da chamada
        apiCallLiveData.observe(this, apiResponse -> {
            viewModel.registrationAttemptFinished();

            if (apiResponse == null) {
                Toast.makeText(this, "Erro: Resposta inesperada do servidor.", Toast.LENGTH_LONG).show();
                return;
            }

            if (apiResponse.isSuccess()) {
                Toast.makeText(this, "Cadastro realizado com sucesso! Verifique seu e-mail.", Toast.LENGTH_LONG).show();

                // **IMPORTANTE: Salvar Credencial com Credential Manager (Opcional, mas recomendado)**
                // Se você implementou o salvamento na ViewModel, chame aqui:
                // viewModel.saveCredentialsAfterRegistration(email, senha);

                // Navegar para a próxima tela (ex: Login ou tela "Verifique seu e-mail")
                 Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class); // Ex: Ir para Login
                 startActivity(intent);
                 finish(); // Fecha a tela de cadastro
            } else {
                // Falha no cadastro da API
                // Exibe a mensagem de erro retornada pela API
                List<String> apiErrors = new ArrayList<>();
                apiErrors.add(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Erro desconhecido no cadastro.");
                errosCadastro.setText(String.join("\n", apiErrors));
                errosCadastro.setVisibility(TextView.VISIBLE);
                // Poderia usar um Toast também:
                // Toast.makeText(this, "Erro no cadastro: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

