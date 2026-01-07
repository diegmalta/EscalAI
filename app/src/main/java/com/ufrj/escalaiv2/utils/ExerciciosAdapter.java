package com.ufrj.escalaiv2.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.dto.ExercicioResponse;

import java.util.ArrayList;
import java.util.List;

public class ExerciciosAdapter extends RecyclerView.Adapter<ExerciciosAdapter.ExercicioViewHolder> {

    private List<ExercicioResponse> exercicios = new ArrayList<>();
    private OnLikeClickListener likeClickListener;

    public interface OnLikeClickListener {
        void onLikeClick(ExercicioResponse exercicio);
    }

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.likeClickListener = listener;
    }

    public void setExercicios(List<ExercicioResponse> exercicios) {
        this.exercicios = exercicios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        ExercicioResponse exercicio = exercicios.get(position);
        holder.bind(exercicio);
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    class ExercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNomeExercicio;
        private TextView tvDescricao;
        private TextView tvDuracao;
        private TextView tvRepeticoes;
        private TextView tvLikes;
        private Chip chipTipo;
        private Chip chipDificuldade;
        private MaterialButton btnLike;
        private MaterialButton btnAssistir;

        public ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeExercicio = itemView.findViewById(R.id.tvNomeExercicio);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvDuracao = itemView.findViewById(R.id.tvDuracao);
            tvRepeticoes = itemView.findViewById(R.id.tvRepeticoes);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            chipTipo = itemView.findViewById(R.id.chipTipo);
            chipDificuldade = itemView.findViewById(R.id.chipDificuldade);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnAssistir = itemView.findViewById(R.id.btnAssistir);
        }

        public void bind(ExercicioResponse exercicio) {
            tvNomeExercicio.setText(exercicio.getNome());
            tvDescricao.setText(exercicio.getDescricao());
            
            // Duração
            if (exercicio.getDuracaoMinutos() != null) {
                tvDuracao.setText(exercicio.getDuracaoMinutos() + " min");
                tvDuracao.setVisibility(View.VISIBLE);
            } else {
                tvDuracao.setVisibility(View.GONE);
            }
            
            // Repetições
            if (exercicio.getRepeticoes() != null && !exercicio.getRepeticoes().isEmpty()) {
                tvRepeticoes.setText(exercicio.getRepeticoes());
                tvRepeticoes.setVisibility(View.VISIBLE);
            } else {
                tvRepeticoes.setVisibility(View.GONE);
            }
            
            // Likes
            tvLikes.setText(String.valueOf(exercicio.getLikes()));
            
            // Tipo
            if (exercicio.getTipo() != null && !exercicio.getTipo().isEmpty()) {
                chipTipo.setText(exercicio.getTipo());
                chipTipo.setVisibility(View.VISIBLE);
            } else {
                chipTipo.setVisibility(View.GONE);
            }
            
            // Dificuldade
            if (exercicio.getDificuldade() != null && !exercicio.getDificuldade().isEmpty()) {
                chipDificuldade.setText(exercicio.getDificuldade());
                chipDificuldade.setVisibility(View.VISIBLE);
            } else {
                chipDificuldade.setVisibility(View.GONE);
            }
            
            // Botão de like
            btnLike.setOnClickListener(v -> {
                if (likeClickListener != null) {
                    likeClickListener.onLikeClick(exercicio);
                }
            });
            
            // Botão de assistir
            btnAssistir.setOnClickListener(v -> {
                if (exercicio.getVideoUrl() != null && !exercicio.getVideoUrl().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(exercicio.getVideoUrl()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

