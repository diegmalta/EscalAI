package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.ufrj.escalaiv2.R;

public class MenuPrincipalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        Button aguaButton = findViewById(R.id.aguaButton);
        aguaButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AguaActivity.class);
            startActivity(intent);
        });
    }
}