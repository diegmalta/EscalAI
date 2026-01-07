package com.ufrj.escalaiv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.databinding.ActivityLesaoBinding;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.utils.LesaoAdapter;
import com.ufrj.escalaiv2.viewmodel.LesaoListVM;

public class LesaoActivity extends AppCompatActivity implements LesaoAdapter.OnLesaoActionListener {
    private ActivityLesaoBinding binding;
    private LesaoListVM lesaoListVM;
    private LesaoAdapter lesaoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o ViewModel
        lesaoListVM = new ViewModelProvider(this).get(LesaoListVM.class);

        // Configurar o DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lesao);
        binding.setViewModel(lesaoListVM);
        binding.setLifecycleOwner(this);

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Configurar o RecyclerView
        setupRecyclerView();

        // Configurar o FAB
        setupFab();

        // Observar mudanças nos dados
        observeData();
    }

    private void setupRecyclerView() {
        lesaoAdapter = new LesaoAdapter(this, null);
        lesaoAdapter.setOnLesaoActionListener(this);

        binding.recyclerViewLesoes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewLesoes.setAdapter(lesaoAdapter);
    }

    private void setupFab() {
        binding.fabRegistrarLesao.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarLesaoActivity.class);
            startActivity(intent);
        });
    }

    private void observeData() {
        lesaoListVM.getLesoes().observe(this, lesoes -> {
            android.util.Log.d("LesaoActivity", "Dados recebidos na Activity: " + (lesoes != null ? lesoes.size() : "null"));
            if (lesoes != null) {
                lesaoAdapter.updateLesoes(lesoes);
            }
        });

        lesaoListVM.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                android.util.Log.e("LesaoActivity", "Erro: " + errorMessage);
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditarClick(LesaoResponse.LesaoData lesao) {
        // Implementar edição da lesão
        Intent intent = new Intent(this, RegistrarLesaoActivity.class);
        intent.putExtra("lesao_data", lesao);
        startActivity(intent);
    }

    @Override
    public void onConcluirClick(LesaoResponse.LesaoData lesao) {
        // Implementar conclusão da lesão
        Snackbar.make(binding.getRoot(), "Funcionalidade de conclusão será implementada", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onReabrirClick(LesaoResponse.LesaoData lesao) {
        // Implementar reabertura da lesão
        Snackbar.make(binding.getRoot(), "Funcionalidade de reabertura será implementada", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPreverTempoClick(LesaoResponse.LesaoData lesao) {
        // Abrir tela de registro com dados da lesão para prever
        Intent intent = new Intent(this, RegistrarLesaoActivity.class);
        intent.putExtra("lesao_data", lesao);
        intent.putExtra("show_prediction", true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar lesões quando voltar para a tela
        lesaoListVM.loadLesoes();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
