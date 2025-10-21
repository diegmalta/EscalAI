package com.ufrj.escalaiv2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.dto.AreaCorporalN1Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN2Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN3Response;
import com.ufrj.escalaiv2.utils.ExerciciosAdapter;
import com.ufrj.escalaiv2.viewmodel.ExerciciosVM;

import java.util.ArrayList;
import java.util.List;

public class ExerciciosFragment extends Fragment {

    private ExerciciosVM viewModel;
    private ExerciciosAdapter adapter;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private LinearLayout emptyStateLayout;
    private ChipGroup chipGroupFiltrosAtivos;
    
    private Chip chipTodos;
    private Chip chipFiltroTipo;
    private Chip chipFiltroAreaN1;
    private Chip chipFiltroDificuldade;

    private List<String> tiposDisponiveis = new ArrayList<>();
    private List<AreaCorporalN1Response> areasN1Disponiveis = new ArrayList<>();
    private List<AreaCorporalN2Response> areasN2Disponiveis = new ArrayList<>();
    private List<AreaCorporalN3Response> areasN3Disponiveis = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercicios, container, false);

        // Inicializa views
        recyclerView = view.findViewById(R.id.recyclerViewExercicios);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        chipGroupFiltrosAtivos = view.findViewById(R.id.chipGroupFiltrosAtivos);
        
        chipTodos = view.findViewById(R.id.chipTodos);
        chipFiltroTipo = view.findViewById(R.id.chipFiltroTipo);
        chipFiltroAreaN1 = view.findViewById(R.id.chipFiltroAreaN1);
        chipFiltroDificuldade = view.findViewById(R.id.chipFiltroDificuldade);

        // Configura RecyclerView
        adapter = new ExerciciosAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Configura listener de like
        adapter.setOnLikeClickListener(exercicio -> {
            viewModel.darLike(exercicio.getId());
            Toast.makeText(getContext(), "Like registrado!", Toast.LENGTH_SHORT).show();
        });

        // Inicializa ViewModel
        viewModel = new ViewModelProvider(this).get(ExerciciosVM.class);

        // Observa mudanças
        setupObservers();

        // Configura filtros
        setupFiltros();

        // Carrega dados iniciais
        carregarDadosIniciais();

        return view;
    }

    private void setupObservers() {
        // Observa exercícios
        viewModel.getExercicios().observe(getViewLifecycleOwner(), exercicios -> {
            progressIndicator.setVisibility(View.GONE);
            
            if (exercicios != null && !exercicios.isEmpty()) {
                adapter.setExercicios(exercicios);
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        });

        // Observa tipos
        viewModel.getTipos().observe(getViewLifecycleOwner(), tipos -> {
            if (tipos != null) {
                tiposDisponiveis = tipos;
            }
        });

        // Observa áreas N1
        viewModel.getAreasN1().observe(getViewLifecycleOwner(), areas -> {
            if (areas != null) {
                areasN1Disponiveis = areas;
            }
        });

        // Observa áreas N2
        viewModel.getAreasN2().observe(getViewLifecycleOwner(), areas -> {
            if (areas != null) {
                areasN2Disponiveis = areas;
            }
        });

        // Observa áreas N3
        viewModel.getAreasN3().observe(getViewLifecycleOwner(), areas -> {
            if (areas != null) {
                areasN3Disponiveis = areas;
            }
        });
    }

    private void setupFiltros() {
        // Chip "Todos" - limpa filtros
        chipTodos.setOnClickListener(v -> {
            viewModel.limparFiltros();
            limparChipsFiltrosAtivos();
            recarregarExercicios();
        });

        // Chip filtro tipo
        chipFiltroTipo.setOnClickListener(v -> mostrarDialogoFiltroTipo());

        // Chip filtro área corporal
        chipFiltroAreaN1.setOnClickListener(v -> mostrarDialogoFiltroAreaN1());

        // Chip filtro dificuldade
        chipFiltroDificuldade.setOnClickListener(v -> mostrarDialogoFiltroDificuldade());
    }

    private void carregarDadosIniciais() {
        mostrarLoading();
        viewModel.carregarTipos();
        viewModel.carregarAreasN1();
        viewModel.carregarExercicios();
    }

    private void recarregarExercicios() {
        mostrarLoading();
        viewModel.carregarExercicios();
    }

    private void mostrarLoading() {
        progressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void mostrarDialogoFiltroTipo() {
        if (tiposDisponiveis.isEmpty()) {
            Toast.makeText(getContext(), "Carregando tipos...", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] tipos = tiposDisponiveis.toArray(new String[0]);
        int checkedItem = -1;
        
        if (viewModel.getFiltroTipo() != null) {
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equals(viewModel.getFiltroTipo())) {
                    checkedItem = i;
                    break;
                }
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filtrar por Tipo")
                .setSingleChoiceItems(tipos, checkedItem, (dialog, which) -> {
                    viewModel.setFiltroTipo(tipos[which]);
                    adicionarChipFiltroAtivo("Tipo: " + tipos[which], "tipo");
                    recarregarExercicios();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar", (dialog, which) -> {
                    viewModel.setFiltroTipo(null);
                    removerChipFiltroAtivo("tipo");
                    recarregarExercicios();
                })
                .show();
    }

    private void mostrarDialogoFiltroAreaN1() {
        if (areasN1Disponiveis.isEmpty()) {
            Toast.makeText(getContext(), "Carregando áreas corporais...", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] areas = new String[areasN1Disponiveis.size()];
        for (int i = 0; i < areasN1Disponiveis.size(); i++) {
            areas[i] = areasN1Disponiveis.get(i).getNome();
        }

        int checkedItem = -1;
        if (viewModel.getFiltroN1() != null) {
            for (int i = 0; i < areasN1Disponiveis.size(); i++) {
                if (areasN1Disponiveis.get(i).getId() == viewModel.getFiltroN1()) {
                    checkedItem = i;
                    break;
                }
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filtrar por Área Corporal")
                .setSingleChoiceItems(areas, checkedItem, (dialog, which) -> {
                    AreaCorporalN1Response areaSelecionada = areasN1Disponiveis.get(which);
                    viewModel.setFiltroN1(areaSelecionada.getId());
                    viewModel.setFiltroN2(null);
                    viewModel.setFiltroN3(null);
                    
                    // Carrega as áreas N2 para esta N1
                    viewModel.carregarAreasN2(areaSelecionada.getId());
                    
                    adicionarChipFiltroAtivo("Área: " + areaSelecionada.getNome(), "area");
                    recarregarExercicios();
                    
                    // Mostra diálogo de N2
                    dialog.dismiss();
                    mostrarDialogoFiltroAreaN2();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar", (dialog, which) -> {
                    viewModel.setFiltroN1(null);
                    viewModel.setFiltroN2(null);
                    viewModel.setFiltroN3(null);
                    removerChipFiltroAtivo("area");
                    recarregarExercicios();
                })
                .show();
    }

    private void mostrarDialogoFiltroAreaN2() {
        if (areasN2Disponiveis.isEmpty()) {
            return; // Não há N2 disponível
        }

        String[] areas = new String[areasN2Disponiveis.size() + 1];
        areas[0] = "Todas";
        for (int i = 0; i < areasN2Disponiveis.size(); i++) {
            areas[i + 1] = areasN2Disponiveis.get(i).getNome();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filtrar por Área Específica")
                .setSingleChoiceItems(areas, 0, (dialog, which) -> {
                    if (which == 0) {
                        // Todas - mantém apenas N1
                        viewModel.setFiltroN2(null);
                        viewModel.setFiltroN3(null);
                    } else {
                        AreaCorporalN2Response areaSelecionada = areasN2Disponiveis.get(which - 1);
                        viewModel.setFiltroN2(areaSelecionada.getId());
                        viewModel.setFiltroN3(null);
                        
                        // Carrega as áreas N3 para esta N2
                        viewModel.carregarAreasN3(areaSelecionada.getId());
                        
                        // Mostra diálogo de N3
                        dialog.dismiss();
                        mostrarDialogoFiltroAreaN3();
                        return;
                    }
                    recarregarExercicios();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoFiltroAreaN3() {
        if (areasN3Disponiveis.isEmpty()) {
            recarregarExercicios();
            return; // Não há N3 disponível
        }

        String[] areas = new String[areasN3Disponiveis.size() + 1];
        areas[0] = "Todas";
        for (int i = 0; i < areasN3Disponiveis.size(); i++) {
            areas[i + 1] = areasN3Disponiveis.get(i).getNome();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filtrar por Área Detalhada")
                .setSingleChoiceItems(areas, 0, (dialog, which) -> {
                    if (which == 0) {
                        // Todas - mantém apenas N2
                        viewModel.setFiltroN3(null);
                    } else {
                        AreaCorporalN3Response areaSelecionada = areasN3Disponiveis.get(which - 1);
                        viewModel.setFiltroN3(areaSelecionada.getId());
                    }
                    recarregarExercicios();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoFiltroDificuldade() {
        String[] dificuldades = {"Fácil", "Moderado", "Difícil"};
        int checkedItem = -1;
        
        if (viewModel.getFiltroDificuldade() != null) {
            for (int i = 0; i < dificuldades.length; i++) {
                if (dificuldades[i].equals(viewModel.getFiltroDificuldade())) {
                    checkedItem = i;
                    break;
                }
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filtrar por Dificuldade")
                .setSingleChoiceItems(dificuldades, checkedItem, (dialog, which) -> {
                    viewModel.setFiltroDificuldade(dificuldades[which]);
                    adicionarChipFiltroAtivo("Dificuldade: " + dificuldades[which], "dificuldade");
                    recarregarExercicios();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar", (dialog, which) -> {
                    viewModel.setFiltroDificuldade(null);
                    removerChipFiltroAtivo("dificuldade");
                    recarregarExercicios();
                })
                .show();
    }

    private void adicionarChipFiltroAtivo(String texto, String tag) {
        // Remove chip antigo com a mesma tag
        removerChipFiltroAtivo(tag);
        
        Chip chip = new Chip(requireContext());
        chip.setText(texto);
        chip.setTag(tag);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupFiltrosAtivos.removeView(chip);
            
            // Limpa o filtro correspondente
            switch (tag) {
                case "tipo":
                    viewModel.setFiltroTipo(null);
                    break;
                case "area":
                    viewModel.setFiltroN1(null);
                    viewModel.setFiltroN2(null);
                    viewModel.setFiltroN3(null);
                    break;
                case "dificuldade":
                    viewModel.setFiltroDificuldade(null);
                    break;
            }
            
            recarregarExercicios();
            
            if (chipGroupFiltrosAtivos.getChildCount() == 0) {
                chipGroupFiltrosAtivos.setVisibility(View.GONE);
            }
        });
        
        chipGroupFiltrosAtivos.addView(chip);
        chipGroupFiltrosAtivos.setVisibility(View.VISIBLE);
    }

    private void removerChipFiltroAtivo(String tag) {
        for (int i = 0; i < chipGroupFiltrosAtivos.getChildCount(); i++) {
            View child = chipGroupFiltrosAtivos.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals(tag)) {
                chipGroupFiltrosAtivos.removeView(child);
                break;
            }
        }
        
        if (chipGroupFiltrosAtivos.getChildCount() == 0) {
            chipGroupFiltrosAtivos.setVisibility(View.GONE);
        }
    }

    private void limparChipsFiltrosAtivos() {
        chipGroupFiltrosAtivos.removeAllViews();
        chipGroupFiltrosAtivos.setVisibility(View.GONE);
    }
}

