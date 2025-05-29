package com.ufrj.escalaiv2.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.ui.HomeFragment;
import com.ufrj.escalaiv2.ui.RelatoriosFragment;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView txtNomeUsuario;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        int currentUserId = getCurrentUserId();
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            String userName = db.usuarioDao().getUserNameById(currentUserId);
            runOnUiThread(() -> txtNomeUsuario.setText(userName != null ? userName : "Usuário"));
        }).start();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.relatorios) {
                    selectedFragment = new RelatoriosFragment();
                } else if (itemId == R.id.exercises) {
                    // selectedFragment = new ExerciciosFragment();
                } else if (itemId == R.id.profile) {
                    // selectedFragment = new PerfilFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private int getCurrentUserId() {
        // Implement this method to return the current user's ID
        return 1; // Substitua pela lógica real
    }
}

