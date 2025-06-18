package com.ufrj.escalaiv2.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ufrj.escalaiv2.databinding.ItemPainReportBinding;
import com.ufrj.escalaiv2.model.PainReportItem;

import java.util.Locale;

public class PainReportAdapter extends ListAdapter<PainReportItem, PainReportAdapter.PainViewHolder> {

    public PainReportAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<PainReportItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PainReportItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PainReportItem oldItem, @NonNull PainReportItem newItem) {
            return oldItem.getLocalDor().equals(newItem.getLocalDor());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PainReportItem oldItem, @NonNull PainReportItem newItem) {
            return oldItem.getLocalDor().equals(newItem.getLocalDor()) &&
                   oldItem.getTempoDesdeInicio().equals(newItem.getTempoDesdeInicio()) &&
                   oldItem.getIntensidadeMaxima() == newItem.getIntensidadeMaxima() &&
                   oldItem.getIntensidadeMedia() == newItem.getIntensidadeMedia() &&
                   oldItem.getTempoSemDor().equals(newItem.getTempoSemDor());
        }
    };

    @NonNull
    @Override
    public PainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPainReportBinding binding = ItemPainReportBinding.inflate(inflater, parent, false);
        return new PainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PainViewHolder holder, int position) {
        PainReportItem currentItem = getItem(position);
        holder.bind(currentItem);
    }

    static class PainViewHolder extends RecyclerView.ViewHolder {
        private final ItemPainReportBinding binding;

        PainViewHolder(ItemPainReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PainReportItem item) {
            binding.tvLocalDor.setText(item.getLocalDor());
            binding.tvTempoDesdeInicio.setText(item.getTempoDesdeInicio());
            binding.tvIntensidadeMaxima.setText(String.valueOf(item.getIntensidadeMaxima()));
            binding.tvIntensidadeMedia.setText(String.format(Locale.getDefault(), "%.1f", item.getIntensidadeMedia()));
            binding.tvTempoSemDor.setText(item.getTempoSemDor());
        }
    }
}
