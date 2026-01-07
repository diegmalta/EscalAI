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
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.dto.LesaoResponse;
import com.ufrj.escalaiv2.enums.AreaCorporalN1;
import com.ufrj.escalaiv2.enums.AreaCorporalN2;
import com.ufrj.escalaiv2.enums.AreaCorporalN3;

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
        void onPreverTempoClick(LesaoResponse.LesaoData lesao);
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
        android.util.Log.d("LesaoAdapter", "updateLesoes chamado com: " + (novasLesoes != null ? novasLesoes.size() : "null") + " lesões");
        this.lesoes = novasLesoes != null ? novasLesoes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateIntervaloConfianca(int lesaoId, double min, double max) {
        for (int i = 0; i < lesoes.size(); i++) {
            if (lesoes.get(i).getId() == lesaoId) {
                notifyItemChanged(i);
                break;
            }
        }
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
        private TextView tvIntervaloConfianca;
        private LinearLayout intervaloConfiancaContainer;

        private MaterialButton btnEditarLesao;
        private MaterialButton btnConcluirLesao;
        private MaterialButton btnPreverTempo;
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
            tvIntervaloConfianca = itemView.findViewById(R.id.tvIntervaloConfianca);
            intervaloConfiancaContainer = itemView.findViewById(R.id.intervaloConfiancaContainer);
            btnEditarLesao = itemView.findViewById(R.id.btnEditarLesao);
            btnConcluirLesao = itemView.findViewById(R.id.btnConcluirLesao);
            btnPreverTempo = itemView.findViewById(R.id.btnPreverTempo);
            cardHeader = itemView.findViewById(R.id.cardHeader);

            cardHeader.setOnClickListener(v -> toggleExpansion());
        }

        public void bind(LesaoResponse.LesaoData lesao) {
            android.util.Log.d("LesaoAdapter", "Bind lesão ID: " + lesao.getId() +
                ", area1: " + lesao.getAreaLesaoN1() +
                ", area2: " + lesao.getAreaLesaoN2() +
                ", area3: " + lesao.getAreaLesaoN3() +
                ", createdAt: " + lesao.getCreatedAt());

            // Configurar área da lesão
            String areaString = getAreaLesaoString(lesao);
            tvAreaLesao.setText(areaString);

            // Configurar data
            tvDataLesao.setText(formatCreatedDate(lesao.getCreatedAt()));

            // Calcular tempo atual em dias
            long diasDesdeCriacao = calcularDiasDesdeCriacao(lesao.getCreatedAt());
            if (diasDesdeCriacao == 0) {
                tvTempoAtual.setText("Hoje");
            } else if (diasDesdeCriacao == 1) {
                tvTempoAtual.setText("1 dia");
            } else {
                tvTempoAtual.setText(diasDesdeCriacao + " dias");
            }

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

            // Configurar intervalo de confiança (se disponível)
            configurarIntervaloConfianca(lesao);

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

            btnPreverTempo.setOnClickListener(v -> {
                if (listener != null) listener.onPreverTempoClick(lesao);
            });
        }

        private void configurarIntervaloConfianca(LesaoResponse.LesaoData lesao) {
            // Por enquanto, vamos esconder o container
            // Quando a previsão for feita, este método será chamado com os dados reais
            intervaloConfiancaContainer.setVisibility(View.GONE);

            // TODO: Implementar quando a previsão for feita
            // if (lesao.getPrevisaoAfastamento() != null) {
            //     double min = lesao.getPrevisaoAfastamento().getIntervaloConfiancaMin();
            //     double max = lesao.getPrevisaoAfastamento().getIntervaloConfiancaMax();
            //     tvIntervaloConfianca.setText(String.format("Intervalo: %.1f - %.1f dias", min, max));
            //     intervaloConfiancaContainer.setVisibility(View.VISIBLE);
            // }
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

        private String formatCreatedDate(String createdAt) {
            if (createdAt == null || createdAt.isEmpty()) {
                return "Data não disponível";
            }

            try {
                // Tentar diferentes formatos de data, incluindo ISO
                String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", // ISO com microssegundos
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",    // ISO com milissegundos
                    "yyyy-MM-dd'T'HH:mm:ss",        // ISO sem frações
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
                };

                for (String format : formats) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                        Date date = inputFormat.parse(createdAt);
                        return outputFormat.format(date).toUpperCase();
                    } catch (Exception e) {
                        // Continua para o próximo formato
                    }
                }

                return "Data inválida";
            } catch (Exception e) {
                return "Data inválida";
            }
        }

        private long calcularDiasDesdeCriacao(String createdAt) {
            if (createdAt == null || createdAt.isEmpty()) {
                return 0;
            }

            try {
                // Tentar diferentes formatos de data, incluindo ISO
                String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", // ISO com microssegundos
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",    // ISO com milissegundos
                    "yyyy-MM-dd'T'HH:mm:ss",        // ISO sem frações
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
                };

                for (String format : formats) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                        Date dataCriacao = inputFormat.parse(createdAt);
                        Date agora = new Date();
                        long diffInMillies = agora.getTime() - dataCriacao.getTime();
                        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        // Continua para o próximo formato
                    }
                }

                return 0;
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
            // Baseado no campo dataConclusao - se não for null/vazio, está concluída
            String dataConclusao = lesao.getDataConclusao();
            return dataConclusao != null && !dataConclusao.trim().isEmpty() && !dataConclusao.equals("null");
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

        private String getAreaLesaoString(LesaoResponse.LesaoData lesao) {
            // Obter área N1 (área principal)
            AreaCorporalN1 areaN1 = AreaCorporalN1.getById(lesao.getAreaLesaoN1());
            if (areaN1 == null) {
                return "Área não especificada";
            }

            // Obter área N2 (subárea) - usar índice baseado em area2 dentro das subáreas de area1
            AreaCorporalN2 areaN2 = null;
            AreaCorporalN2[] subareas = AreaCorporalN2.getByRegiaoCorporalId(lesao.getAreaLesaoN1());
            if (lesao.getAreaLesaoN2() >= 0 && lesao.getAreaLesaoN2() < subareas.length) {
                areaN2 = subareas[lesao.getAreaLesaoN2()];
            }

            // Obter área N3 (especificação) - usar o id da subárea para buscar as especificações
            if (areaN2 != null) {
                AreaCorporalN3[] especificacoes = AreaCorporalN3.getByAreaRegiaoCorporalId(areaN2.getId());
                if (lesao.getAreaLesaoN3() >= 0 && lesao.getAreaLesaoN3() < especificacoes.length) {
                    return especificacoes[lesao.getAreaLesaoN3()].getNome();
                }
            }

            return "Área não especificada";
        }
    }
}