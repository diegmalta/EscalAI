package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

import com.ufrj.escalaiv2.R;
import com.google.android.material.textfield.TextInputLayout;
import com.ufrj.escalaiv2.controller.InfoUsuariosController;

public class FormInfoUsuariosActivity extends AppCompatActivity {

    private InfoUsuariosController infoUsuariosController;
    private TextInputLayout nomeCompleto, peso, altura;
    private AutoCompleteTextView pesoDropdown, alturaDropdown;
    private RadioGroup escaladorRadioGroup;
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form);

        infoUsuariosController = InfoUsuariosController.getInfoUsuariosController(this);

        // Inicializar os campos da View
        this.nomeCompleto = findViewById(R.id.inputNomeCompleto);
        this.peso = findViewById(R.id.inputPeso);
        //this.pesoDropdown = findViewById(R.id.weightOptions);
        this.altura = findViewById(R.id.inputAltura);
        //this.alturaDropdown = findViewById(R.id.heightOptions);
        this.escaladorRadioGroup = findViewById(R.id.radioGroup);
        this.submitButton = findViewById(R.id.confirmDataButton);

        // Configurar os menus suspensos (dropdowns)
        //infoUsuariosController.setupDropdownMenu(this.pesoDropdown, R.array.weights_measures);
        //infoUsuariosController.setupDropdownMenu(this.alturaDropdown, R.array.height_measures);
    }
    public void confirmarDados(View v){
        submitButton.setOnClickListener(view -> {
            infoUsuariosController.handleSubmit(peso, pesoDropdown,
                    altura, alturaDropdown, escaladorRadioGroup);

        // Navegar para MenuPrincipalActivity
        Intent intent = new Intent(FormInfoUsuariosActivity.this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish();
        });
    }
}
