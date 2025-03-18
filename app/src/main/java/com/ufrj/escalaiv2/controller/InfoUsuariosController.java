package com.ufrj.escalaiv2.controller;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ufrj.escalaiv2.R;
import com.google.android.material.textfield.TextInputLayout;
import com.ufrj.escalaiv2.model.Usuario;

public class InfoUsuariosController {

    private static InfoUsuariosController infoUsuariosController;
    private final Context context;
    private Usuario usuario;

    public InfoUsuariosController(Context context, Usuario usuario) {
        this.context = context;
        this.usuario = usuario;
    }

    public InfoUsuariosController(Context context) {
        this.context = context;
    }

    public static synchronized InfoUsuariosController getInfoUsuariosController(Context context) {
        if (infoUsuariosController == null) {
            infoUsuariosController = new InfoUsuariosController(context.getApplicationContext());
        }
        return infoUsuariosController;
    }

    public void setupDropdownMenu(AutoCompleteTextView autoCompleteTextView, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                arrayResourceId, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);
    }

    public void handleSubmit(TextInputLayout pesoEditText, AutoCompleteTextView pesoDropdown,
                             TextInputLayout alturaEditText, AutoCompleteTextView alturaDropdown, RadioGroup escaladorRadioGroup) {
        try {
            double peso = Float.parseFloat(pesoEditText.getEditText().toString());
            if(pesoDropdown.getText().toString().equals(context.getString(R.string.pounds))){
                peso = peso * 0.4535924;
            }
            usuario.setPeso(peso);
            double altura = Float.parseFloat(alturaEditText.getEditText().toString());
            if(alturaDropdown.getText().toString().equals(context.getString(R.string.feet))){
                altura = altura/30.48;
            }
            usuario.setAltura(altura);

            int selectedId = escaladorRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = escaladorRadioGroup.findViewById(selectedId);
            if(selectedRadioButton.isChecked()){
                //TODO: GRAU DE ESCALADA E FREQUÊNCIA
                // usuario.setGrauEscalada(grauEscaladaDropdown.getText().toString());
                // usuario.setFrequenciaEscalada(frequenciaEscaladaDropdown.getText().toString());
            }
            //TODO: SALVAR NO INFOS NO BANCO
            Toast.makeText(context, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
