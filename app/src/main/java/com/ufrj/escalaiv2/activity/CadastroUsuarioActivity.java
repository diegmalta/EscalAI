package com.ufrj.escalaiv2.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.viewmodel.CadastroUsuarioVM;
import com.ufrj.escalaiv2.viewmodel.CadastroUsuarioVMFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private EditText emailEdit, nomeEdit, sobrenomeEdit, dataNascimentoEdit,
            celularEdit, senhaEdit, confirmaSenhaEdit;
    private TextView errosCadastro;
    private CheckBox agreeToTermsCheckBox;
    private Button cadastrarButton;
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
        initializeViewModel(); // Call this before using viewModel
        initializeViews();
        setupObservers();
        setupDatePicker();
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
        agreeToTermsCheckBox = findViewById(R.id.agreeToTermsCheckBox);
        cadastrarButton = findViewById(R.id.cadastrarButton);

        cadastrarButton.setOnClickListener(v -> cadastrarUsuario());
    }

    private void setupObservers() {
        viewModel.getErros().observe(this, erros -> {
            if (!erros.isEmpty()) {
                errosCadastro.setText(String.join("\n", erros));
                errosCadastro.setVisibility(TextView.VISIBLE);
            }
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
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());
        dataNascimentoEdit.setText(formattedDate);
        viewModel.setDataNascimento(formattedDate);
    }

    private void cadastrarUsuario() {
        viewModel.cadastrarUsuario(
                emailEdit.getText().toString(),
                nomeEdit.getText().toString(),
                sobrenomeEdit.getText().toString(),
                dataNascimentoEdit.getText().toString(),
                celularEdit.getText().toString(),
                senhaEdit.getText().toString(),
                confirmaSenhaEdit.getText().toString(),
                agreeToTermsCheckBox.isChecked()
        );
    }
}