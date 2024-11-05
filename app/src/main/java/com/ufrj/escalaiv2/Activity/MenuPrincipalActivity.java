package com.ufrj.escalaiv2.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.escalaiv2.R;

public class MenuPrincipalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }

    // Configurar o botão de consumo de água
    val botaoAgua = findViewById<Button>(R.id.waterConsumptionButton)
            botaoAgua.setOnClickListener {
        val intent = Intent(this, AguaActivity::class.java)
        startActivity(intent)
    }
}
