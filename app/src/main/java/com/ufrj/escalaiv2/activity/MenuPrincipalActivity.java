package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.model.AppDatabase;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView txtNomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);

        // Assuming you have a way to get the current user's ID
        int currentUserId = getCurrentUserId(); // Implement this method to get the current user ID

        // Fetch the user's name from the database
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            String userName = db.usuarioDao().getUserNameById(currentUserId);
            runOnUiThread(() -> txtNomeUsuario.setText(userName));
        }).start();

        Button aguaButton = findViewById(R.id.aguaButton);
        aguaButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AguaActivity.class);
            startActivity(intent);
        });

        Button humorButton = findViewById(R.id.humorButton);
        humorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, HumorActivity.class);
            startActivity(intent);
        });

        Button dorButton = findViewById(R.id.dorButton);
        dorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, DorActivity.class);
            startActivity(intent);
        });
    }

    private int getCurrentUserId() {
        // Implement this method to return the current user's ID
        return 1; // Replace with actual logic to get the current user ID
    }
}