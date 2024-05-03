package com.ufrj.escalaiv2.ViewModel;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.escalaiv2.R;
import com.ufrj.escalaiv2.dao.UsuarioDAO;
import com.ufrj.escalaiv2.Usuario;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CadastroUsuarioVM extends AppCompatActivity {

    private EditText email, nome, sobrenome, celular, senha, confirmaSenha;
    private Button voltarButton;
    private TextView errosCadastro, dataNascimento;

    private CheckBox checkboxTermosDeUso;

    private View termsOfUseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        this.email = findViewById(R.id.email);
        this.nome = findViewById(R.id.nome);
        this.sobrenome = findViewById(R.id.sobrenome);
        this.dataNascimento = findViewById(R.id.dataNascimento);
        this.celular = findViewById(R.id.celular);
        this.senha = findViewById(R.id.senha);
        this.confirmaSenha = findViewById(R.id.confirmaSenha);
        Button cadastrarButton = findViewById(R.id.cadastrarButton);
        this.errosCadastro = findViewById(R.id.errosCadastro);
        this.checkboxTermosDeUso = findViewById(R.id.agreeToTermsCheckBox);

        cadastrarButton.setOnClickListener(v -> {
            try {
                cadastrar();
            } catch (SQLException | ClassNotFoundException | ParseException |
                     InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        // View de Termos de Uso
        String termsOfUse = " Acessar termos de uso.";
        SpannableString spannableString = new SpannableString(getString(R.string.termsAgreement) + termsOfUse);
        termsOfUseView = getLayoutInflater().inflate(R.layout.terms_of_use, null);
        FrameLayout mainLayout = findViewById(R.id.cadastroUsuarioLayout);
        mainLayout.addView(termsOfUseView);
        termsOfUseView.setVisibility(View.GONE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                termsOfUseView.setVisibility(View.VISIBLE);
            }
        };

        // Fazer o link da View de termos de uso com cor azul e sublinhado
        spannableString.setSpan(clickableSpan, spannableString.length() - termsOfUse.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkboxTermosDeUso.setText(spannableString);
        checkboxTermosDeUso.setMovementMethod(LinkMovementMethod.getInstance());

        // Comportamento do botão de voltar do sistema faz um callback à tela anterior, ao sair da tela de termos de uso
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (termsOfUseView.getVisibility() == View.VISIBLE) {
                    termsOfUseView.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearDate, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearDate);
                    EditText editTextDataNascimento = findViewById(R.id.dataNascimento);
                    editTextDataNascimento.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void cadastrar() throws SQLException, ClassNotFoundException, ParseException, InterruptedException {
        List<String> erros = errosCadastro();
        if (!erros.isEmpty()) {
            errosCadastro.setText(erros.stream().collect(Collectors.joining(System.lineSeparator())));
            return;
        }
        Usuario usuario = new Usuario();
        if (!this.email.getText().toString().equals("") && !this.nome.getText().toString().equals("") && !this.sobrenome.getText().toString().equals("")
                && !this.celular.getText().toString().equals("") && !this.senha.getText().toString().equals("") &&
                !this.confirmaSenha.getText().toString().equals("")) {
            usuario.setEmail(this.email.getText().toString());
            usuario.setNome(this.nome.getText().toString());
            usuario.setSobrenome(this.sobrenome.getText().toString());
            usuario.setDataNasc(this.dataNascimento.getText().toString());
            usuario.setCelular(this.celular.getText().toString());
            usuario.setSenha(this.senha.getText().toString());
            UsuarioDAO dao = new UsuarioDAO();
            //dao.cadastrarUsuario(usuario);
        } else {
            errosCadastro.setText(R.string.signUpError_FieldsNotFilledIn);
        }
    }

    private List<String> errosCadastro() throws ParseException, InterruptedException {
        List<String> erros = new ArrayList<>();
        if (email.getText().toString().isEmpty() || nome.getText().toString().isEmpty() || dataNascimento.getText().toString().isEmpty() ||
                celular.getText().toString().isEmpty() || senha.getText().toString().isEmpty() ||
                confirmaSenha.getText().toString().isEmpty() ||
                !confirmaSenha.getText().toString().equals(senha.getText().toString())) {
            erros.add("É necessário preencher todos os campos.");
        }
        if (!Pattern.compile("[@]").matcher(this.email.getText().toString()).find(0) && !Pattern.compile("[.]").matcher(this.email.getText().toString()).find(0)) {
            erros.add("É necessário inserir um email válido.");
        }
        if (!this.senha.getText().toString().isEmpty()) {
            if (this.senha.getText().toString().length() < 8) {
                erros.add("A senha deve possuir mais de 8 caracteres.");
            }
            if (!Pattern.compile("[A-Z]").matcher(this.senha.getText().toString()).find()) {
                erros.add("A senha deve possuir ao menos 1 caractere letra maiúscula.");
            }
            if (!Pattern.compile("[a-z]").matcher(this.senha.getText().toString()).find()) {
                erros.add("A senha deve possuir ao menos 1 caractere letra minúscula.");
            }
            if (!Pattern.compile("[0-9]").matcher(this.senha.getText().toString()).find()) {
                erros.add("A senha deve possuir ao menos 1 caractere numérico.");
            }
            if (!this.confirmaSenha.getText().toString().equals(this.senha.getText().toString())) {
                erros.add("As senhas devem ser iguais.");
            }
        }
        if (!this.checkboxTermosDeUso.isChecked()) {
            erros.add("É necessário aceitar os termos de uso.");
        }
        if (!this.dataNascimento.getText().toString().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date dateOfBirth = dateFormat.parse(this.dataNascimento.getText().toString());
            Calendar calendarDateOfBirth = Calendar.getInstance();
            calendarDateOfBirth.setTime(dateOfBirth);

            Calendar now = Calendar.getInstance();
            int idade = now.get(Calendar.YEAR) - calendarDateOfBirth.get(Calendar.YEAR);

            if (idade < 18) {
                erros.add("Você deve ter pelo menos 18 anos para se cadastrar.");
            }
        }
        return erros;
    }

    private void voltar(View v) {
        finish();
    }
}