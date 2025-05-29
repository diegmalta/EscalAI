package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ufrj.escalaiv2.R;

public class TermsOfUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use);
        setupBackButton();
    }
    
    private void setupBackButton() {
        View headerView = findViewById(R.id.header);
        if (headerView != null) {
            // Você pode adicionar um botão de voltar programaticamente ou
            // simplesmente usar o botão de voltar do sistema Android
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
