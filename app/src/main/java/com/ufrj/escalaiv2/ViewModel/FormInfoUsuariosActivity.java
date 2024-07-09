package com.ufrj.escalaiv2.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

import com.example.escalaiv2.R;
import com.ufrj.escalaiv2.Controller.InfoUsuariosController;

public class FormInfoUsuariosActivity extends AppCompatActivity {


    private InfoUsuariosController infoUsuariosController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form);

        infoUsuariosController = InfoUsuariosController.getInfoUsuariosController(this);

        // Inicializar os campos da View
        EditText nomeCompletoEditText = findViewById(R.id.inputNomeCompleto);
        EditText pesoEditText = findViewById(R.id.inputPeso);
        AutoCompleteTextView pesoDropdown = findViewById(R.id.weightOptions);
        EditText alturaEditText = findViewById(R.id.inputAltura);
        AutoCompleteTextView alturaDropdown = findViewById(R.id.heightOptions);
        RadioGroup escaladorRadioGroup = findViewById(R.id.radioGroup);
        Button submitButton = findViewById(R.id.confirmDataButton);

        // Configurar os menus suspensos (dropdowns)
        infoUsuariosController.setupDropdownMenu(pesoDropdown, R.array.weights_measures);
        infoUsuariosController.setupDropdownMenu(alturaDropdown, R.array.height_measures);

        // Configurar o botão de envio
        // Configurar o botão de envio
        submitButton.setOnClickListener(view -> {
            infoUsuariosController.handleSubmit(nomeCompletoEditText, pesoEditText, pesoDropdown,
                    alturaEditText, alturaDropdown, escaladorRadioGroup);

            // Navegar para MenuPrincipalActivity
            Intent intent = new Intent(FormInfoUsuariosActivity.this, MenuPrincipalActivity.class);
            startActivity(intent);
        });
    }
}
