package com.ufrj.escalaiv2.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.utils.PainReportAdapter; // Importar o novo adapter
import com.ufrj.escalaiv2.databinding.FragmentRelatoriosBinding; // Gerado pelo Data Binding
import com.ufrj.escalaiv2.viewmodel.RelatoriosVM;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RelatoriosFragment extends Fragment {

    private FragmentRelatoriosBinding binding;
    private RelatoriosVM viewModel;
    private PainReportAdapter painReportAdapter; // Adapter para a tabela de dores

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_relatorios, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RelatoriosVM.class);

        setupCharts();
        setupPainTable(); // Configurar a tabela de dores
        setupObservers();
    }

    private void setupCharts() {
        // Configurações iniciais comuns para os gráficos de linha (exceto dor)
        configureLineChart(binding.chartAgua, "Consumo de Água (ml)");
        configureLineChart(binding.chartSono, "Duração do Sono (min)");
        // configureLineChart(binding.chartDor, "Intensidade da Dor"); // REMOVIDO
        configureLineChart(binding.chartHumor, "Níveis de Humor");

        // Configurações iniciais para o gráfico de barras (Treino)
        configureBarChart(binding.chartTreino, "Duração do Treino (min)");
    }

    private void setupPainTable() {
        painReportAdapter = new PainReportAdapter();
        binding.recyclerViewDores.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDores.setAdapter(painReportAdapter);
        binding.recyclerViewDores.setNestedScrollingEnabled(false); // Para funcionar bem dentro do NestedScrollView
    }

    private void configureLineChart(LineChart chart, String description) {
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(description);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new DateAxisValueFormatter());
        chart.getXAxis().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.invalidate();
    }

    private void configureBarChart(BarChart chart, String description) {
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(description);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new DateAxisValueFormatter());
        chart.getXAxis().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.invalidate();
    }

    private void toggleViews(View dataView, View emptyView, boolean hasData) {
        dataView.setVisibility(hasData ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }

    private void setupObservers() {

        viewModel.getWaterConsumptionLast7Days()
                .observe(getViewLifecycleOwner(), map -> {
                    boolean hasData = map != null && !map.isEmpty();
                    toggleViews(binding.chartAgua, binding.tvNoDataAgua, hasData);
                    if (hasData) updateLineChart(binding.chartAgua, map,
                            "Água Consumida", Color.BLUE);
                });

        viewModel.getSleepDurationLast7Days()
                .observe(getViewLifecycleOwner(), map -> {
                    boolean hasData = map != null && !map.isEmpty();
                    toggleViews(binding.chartSono, binding.tvNoDataSono, hasData);
                    if (hasData) updateLineChart(binding.chartSono, map,
                            "Duração do Sono", Color.MAGENTA);
                });

        viewModel.getTrainingDurationLast7Days()
                .observe(getViewLifecycleOwner(), map -> {
                    boolean hasData = map != null && !map.isEmpty();
                    toggleViews(binding.chartTreino, binding.tvNoDataTreino, hasData);
                    if (hasData) updateBarChart(binding.chartTreino, map,
                            "Duração do Treino", Color.GREEN);
                });

        viewModel.getMoodLevelsLast7Days()
                .observe(getViewLifecycleOwner(), map -> {
                    boolean hasData = map != null && !map.isEmpty();
                    toggleViews(binding.chartHumor, binding.tvNoDataHumor, hasData);
                    if (hasData) updateMoodChart(binding.chartHumor, map);
                });

        viewModel.getPainReportData()
                .observe(getViewLifecycleOwner(), list -> {
                    boolean hasData = list != null && !list.isEmpty();
                    painReportAdapter.submitList(list);
                    // tabela e cabeçalho
                    toggleViews(binding.recyclerViewDores, binding.tvNoDataDores, hasData);
                    binding.headerDores.setVisibility(hasData ? View.VISIBLE : View.GONE);
                });
    }

    // Métodos updateLineChart, updateBarChart, updateMoodChart permanecem os mesmos
    private void updateLineChart(LineChart chart, Map<String, Integer> dataMap, String label, int color) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            try {
                Date date = sdf.parse(entry.getKey());
                if (date != null) {
                    entries.add(new Entry(date.getTime(), entry.getValue()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        entries.sort(Comparator.comparing(Entry::getX));

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void updateBarChart(BarChart chart, Map<String, Integer> dataMap, String label, int color) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            try {
                Date date = sdf.parse(entry.getKey());
                if (date != null) {
                    entries.add(new BarEntry(date.getTime(), entry.getValue()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        entries.sort(Comparator.comparing(Entry::getX));

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chart.setData(barData);
        chart.getXAxis().setLabelCount(entries.size());
        chart.invalidate();
    }

    private void updateMoodChart(LineChart chart, Map<String, Map<String, Integer>> dataMap) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int[] colors = {Color.YELLOW, Color.GRAY, Color.MAGENTA, Color.RED, Color.CYAN};
        String[] moodKeys = {"Alegria", "Tristeza", "Ansiedade", "Estresse", "Calma"};

        for (int i = 0; i < moodKeys.length; i++) {
            String mood = moodKeys[i];
            ArrayList<Entry> entries = new ArrayList<>();
            for (Map.Entry<String, Map<String, Integer>> dayEntry : dataMap.entrySet()) {
                try {
                    Date date = sdf.parse(dayEntry.getKey());
                    if (date != null && dayEntry.getValue().containsKey(mood)) {
                        entries.add(new Entry(date.getTime(), dayEntry.getValue().get(mood)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            entries.sort(Comparator.comparing(Entry::getX));
            if (!entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, mood);
                dataSet.setColor(colors[i]);
                dataSet.setCircleColor(colors[i]);
                dataSet.setLineWidth(1.5f);
                dataSet.setCircleRadius(3f);
                dataSet.setDrawCircleHole(false);
                dataSet.setValueTextSize(9f);
                dataSet.setDrawFilled(false);
                dataSet.setMode(LineDataSet.Mode.LINEAR);
                dataSets.add(dataSet);
            }
        }
        if (!dataSets.isEmpty()) {
            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);
            chart.invalidate();
        }
    }

    // Formatter para exibir datas no eixo X
    private static class DateAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        @Override
        public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
            return mFormat.format(new Date((long) value));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitar memory leaks
    }
}

