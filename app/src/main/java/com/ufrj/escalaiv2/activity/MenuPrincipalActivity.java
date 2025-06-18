package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.repository.AuthRepository;
import com.ufrj.escalaiv2.ui.HomeFragment;
import com.ufrj.escalaiv2.ui.RelatoriosFragment;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView txtNomeUsuario;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Inicializa o AuthRepository para gerenciar o logout
        authRepository = new AuthRepository(this);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);

        // Configura o listener para o menu superior
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.item_configuracoes) {
                // Implementação futura para configurações
                Toast.makeText(MenuPrincipalActivity.this, "Configurações", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.item_logout) {
                // Executa o logout
                realizarLogout();
                return true;
            }

            return false;
        });

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

    /**
     * Realiza o logout do usuário:
     * 1. Remove o token de autenticação
     * 2. Limpa dados sensíveis
     * 3. Redireciona para a tela de login
     */
    private void realizarLogout() {
        // Executa o logout no AuthRepository
        authRepository.logout();

        // Exibe mensagem de confirmação
        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();

        // Redireciona para a tela de login
        Intent intent = new Intent(this, MainActivity.class);
        // Limpa o histórico de navegação para evitar voltar com o botão Back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
