package com.ufrj.escalaiv2.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.model.TreinoReportItem;

public class TreinoReportAdapter extends ListAdapter<TreinoReportItem, TreinoReportAdapter.TreinoViewHolder> {

    public TreinoReportAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TreinoReportItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<TreinoReportItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull TreinoReportItem oldItem, @NonNull TreinoReportItem newItem) {
            return oldItem.getTipoTreino().equals(newItem.getTipoTreino()) &&
                   oldItem.getData().equals(newItem.getData());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TreinoReportItem oldItem, @NonNull TreinoReportItem newItem) {
            return oldItem.getTipoTreino().equals(newItem.getTipoTreino()) &&
                   oldItem.getData().equals(newItem.getData()) &&
                   oldItem.getDuracaoMinutos() == newItem.getDuracaoMinutos();
        }
    };

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino_report, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        TreinoReportItem item = getItem(position);
        holder.bind(item);
    }

    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTipoTreino;
        private final TextView tvData;
        private final TextView tvDuracao;

        TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoTreino = itemView.findViewById(R.id.tv_tipo_treino);
            tvData = itemView.findViewById(R.id.tv_data);
            tvDuracao = itemView.findViewById(R.id.tv_duracao);
        }

        void bind(TreinoReportItem item) {
            tvTipoTreino.setText(item.getTipoTreino());
            tvData.setText(item.getData());
            tvDuracao.setText(item.getDuracaoMinutos() + " min");
        }
    }
}