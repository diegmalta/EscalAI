package com.ufrj.escalaiv2.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.dto.LesaoResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class LesaoAdapter extends RecyclerView.Adapter<LesaoAdapter.LesaoViewHolder> {

    private List<LesaoResponse.LesaoData> lesoes;
    private Context context;
    private OnLesaoActionListener listener;

    public interface OnLesaoActionListener {
        void onEditarClick(LesaoResponse.LesaoData lesao);
        void onConcluirClick(LesaoResponse.LesaoData lesao);
        void onReabrirClick(LesaoResponse.LesaoData lesao);
    }

    public LesaoAdapter(Context context, List<LesaoResponse.LesaoData> lesoes) {
        this.context = context;
        this.lesoes = lesoes != null ? lesoes : new ArrayList<>();
    }

    public void setOnLesaoActionListener(OnLesaoActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LesaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesao_card, parent, false);
        return new LesaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LesaoViewHolder holder, int position) {
        LesaoResponse.LesaoData lesao = lesoes.get(position);
        holder.bind(lesao);
    }

    @Override
    public int getItemCount() {
        return lesoes != null ? lesoes.size() : 0;
    }

    public void updateLesoes(List<LesaoResponse.LesaoData> novasLesoes) {
        this.lesoes = novasLesoes != null ? novasLesoes : new ArrayList<>();
        notifyDataSetChanged();
    }

    class LesaoViewHolder extends RecyclerView.ViewHolder {

        private View statusIndicator;
        private TextView tvAreaLesao;
        private TextView tvDataLesao;
        private TextView tvStatusAtual;
        private TextView tvTempoAtual;
        private TextView tvTempoPrevisto;
        private ImageView ivExpandArrow;
        private LinearLayout expandedContent;
        private TextView tvDiagnostico;
        private TextView tvProfissional;
        private TextView tvReincidencia;
        private LinearProgressIndicator progressRecuperacao;
        private TextView tvProgressoPercentual;
        private MaterialButton btnEditarLesao;
        private MaterialButton btnConcluirLesao;
        private LinearLayout cardHeader;

        private boolean isExpanded = false;

        public LesaoViewHolder(@NonNull View itemView) {
            super(itemView);

            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            tvAreaLesao = itemView.findViewById(R.id.tvAreaLesao);
            tvDataLesao = itemView.findViewById(R.id.tvDataLesao);
            tvStatusAtual = itemView.findViewById(R.id.tvStatusAtual);
            tvTempoAtual = itemView.findViewById(R.id.tvTempoAtual);
            tvTempoPrevisto = itemView.findViewById(R.id.tvTempoPrevisto);
            ivExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
            expandedContent = itemView.findViewById(R.id.expandedContent);
            tvDiagnostico = itemView.findViewById(R.id.tvDiagnostico);
            tvProfissional = itemView.findViewById(R.id.tvProfissional);
            tvReincidencia = itemView.findViewById(R.id.tvReincidencia);
            progressRecuperacao = itemView.findViewById(R.id.progressRecuperacao);
            tvProgressoPercentual = itemView.findViewById(R.id.tvProgressoPercentual);
            btnEditarLesao = itemView.findViewById(R.id.btnEditarLesao);
            btnConcluirLesao = itemView.findViewById(R.id.btnConcluirLesao);
            cardHeader = itemView.findViewById(R.id.cardHeader);

            cardHeader.setOnClickListener(v -> toggleExpansion());
        }

        public void bind(LesaoResponse.LesaoData lesao) {
            // Configurar área da lesão
            tvAreaLesao.setText(getAreaLesaoString(lesao));

            // Configurar data
            tvDataLesao.setText(formatCreatedDate(lesao.getCreatedAt()));

            // Calcular tempo atual em dias
            long diasDesdeCreacao = calcularDiasDesdeCreacao(lesao.getCreatedAt());
            tvTempoAtual.setText(diasDesdeCreacao + " dias");

            // Tempo previsto baseado no tipo de lesão (exemplo)
            int tempoPrevisto = calcularTempoPrevisto(lesao);
            tvTempoPrevisto.setText("Prev: " + tempoPrevisto + " dias");

            // Status e cor do indicador
            boolean isConcluida = isConcluida(lesao);
            if (isConcluida) {
                tvStatusAtual.setText("Concluída");
                statusIndicator.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.md_theme_light_primary)
                );
            } else {
                tvStatusAtual.setText("Em andamento");
                statusIndicator.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.md_theme_light_error)
                );
            }

            // Configurar conteúdo expandido
            tvDiagnostico.setText(getDiagnosticoString(lesao.getDiagnostico()));
            tvProfissional.setText(getProfissionalString(lesao.getProfissionalTratamento()));
            tvReincidencia.setText(lesao.isReincidencia() ? "Sim" : "Não");

            // Calcular progresso
            int progresso = 0;
            if (tempoPrevisto > 0) {
                progresso = Math.min(100, (int) ((diasDesdeCreacao * 100) / tempoPrevisto));
            }
            progressRecuperacao.setProgress(progresso);
            tvProgressoPercentual.setText(progresso + "%");

            // Configurar botões
            if (isConcluida) {
                btnConcluirLesao.setText("Reabrir");
                btnConcluirLesao.setOnClickListener(v -> {
                    if (listener != null) listener.onReabrirClick(lesao);
                });
            } else {
                btnConcluirLesao.setText("Concluir");
                btnConcluirLesao.setOnClickListener(v -> {
                    if (listener != null) listener.onConcluirClick(lesao);
                });
            }

            btnEditarLesao.setOnClickListener(v -> {
                if (listener != null) listener.onEditarClick(lesao);
            });
        }

        private void toggleExpansion() {
            isExpanded = !isExpanded;

            if (isExpanded) {
                expandedContent.setVisibility(View.VISIBLE);
                rotateArrow(0, 180);
            } else {
                expandedContent.setVisibility(View.GONE);
                rotateArrow(180, 0);
            }
        }

        private void rotateArrow(float from, float to) {
            RotateAnimation rotate = new RotateAnimation(from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            ivExpandArrow.startAnimation(rotate);
        }

        private String getAreaLesaoString(LesaoResponse.LesaoData lesao) {
            // Mapeamento das áreas baseado nos códigos
            String[] areas = {"", "Ombro", "Cotovelo", "Punho/Mão", "Dedos",
                             "Coluna", "Quadril", "Joelho", "Tornozelo/Pé"};

            StringBuilder resultado = new StringBuilder();

            if (lesao.getAreaLesaoN1() > 0 && lesao.getAreaLesaoN1() < areas.length) {
                resultado.append(areas[lesao.getAreaLesaoN1()]);
            }

            if (lesao.getAreaLesaoN2() > 0 && lesao.getAreaLesaoN2() < areas.length) {
                if (resultado.length() > 0) resultado.append(" + ");
                resultado.append(areas[lesao.getAreaLesaoN2()]);
            }

            if (lesao.getAreaLesaoN3() > 0 && lesao.getAreaLesaoN3() < areas.length) {
                if (resultado.length() > 0) resultado.append(" + ");
                resultado.append(areas[lesao.getAreaLesaoN3()]);
            }

            return resultado.length() > 0 ? resultado.toString() : "Área não especificada";
        }

        private String formatCreatedDate(String createdAt) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                Date date = inputFormat.parse(createdAt);
                return outputFormat.format(date).toUpperCase();
            } catch (Exception e) {
                return "DATA INVÁLIDA";
            }
        }

        private long calcularDiasDesdeCreacao(String createdAt) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date dataCreacao = format.parse(createdAt);
                Date agora = new Date();
                long diffInMillies = agora.getTime() - dataCreacao.getTime();
                return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                return 0;
            }
        }

        private int calcularTempoPrevisto(LesaoResponse.LesaoData lesao) {
            // Lógica para calcular tempo previsto baseado no tipo de lesão
            // Esta é uma implementação de exemplo
            int base = 30; // 30 dias base

            // Ajustar baseado na área da lesão
            if (lesao.getAreaLesaoN1() == 5 || lesao.getAreaLesaoN2() == 5 || lesao.getAreaLesaoN3() == 5) { // Coluna
                base = 45;
            } else if (lesao.getAreaLesaoN1() == 7 || lesao.getAreaLesaoN2() == 7 || lesao.getAreaLesaoN3() == 7) { // Joelho
                base = 35;
            }

            // Ajustar se for reincidência
            if (lesao.isReincidencia()) {
                base = (int) (base * 1.5);
            }

            return base;
        }

        private boolean isConcluida(LesaoResponse.LesaoData lesao) {
            // Lógica para determinar se a lesão está concluída
            // Por exemplo, baseado em algum campo de status
            // Como não temos um campo específico, vamos usar uma heurística temporária
            long dias = calcularDiasDesdeCreacao(lesao.getCreatedAt());
            int tempoPrevisto = calcularTempoPrevisto(lesao);
            return dias >= tempoPrevisto;
        }

        private String getDiagnosticoString(int diagnostico) {
            String[] diagnosticos = {"", "Tendinite", "Entorse", "Distensão muscular",
                                   "Fratura", "Luxação", "Bursite", "Outro"};

            if (diagnostico > 0 && diagnostico < diagnosticos.length) {
                return diagnosticos[diagnostico];
            }
            return "Não especificado";
        }

        private String getProfissionalString(int profissional) {
            String[] profissionais = {"", "Fisioterapeuta", "Ortopedista", "Médico do esporte",
                                    "Fisiatra", "Outro"};

            if (profissional > 0 && profissional < profissionais.length) {
                return profissionais[profissional];
            }
            return "Não especificado";
        }
    }
}