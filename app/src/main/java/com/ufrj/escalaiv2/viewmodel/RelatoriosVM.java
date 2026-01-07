package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ufrj.escalaiv2.dto.DadosUsuarioResponse;
import com.ufrj.escalaiv2.model.PainReportItem;
import com.ufrj.escalaiv2.model.TreinoReportItem;
import com.ufrj.escalaiv2.repository.RelatoriosRepository;

import java.util.List;
import java.util.Map;

public class RelatoriosVM extends AndroidViewModel {

    private static final String TAG = "RelatoriosVM";
    private final RelatoriosRepository repository;
    private final LiveData<DadosUsuarioResponse> dadosUsuario;

    public RelatoriosVM(Application application) {
        super(application);
        repository = new RelatoriosRepository(application);
        dadosUsuario = repository.getDadosUsuario(7); // Busca dados dos últimos 7 dias
    }

    public LiveData<DadosUsuarioResponse> getDadosUsuario() {
        return dadosUsuario;
    }

    // --- LiveData para os gráficos existentes (Água, Sono, Treino, Humor) ---
    public LiveData<Map<String, Integer>> getWaterConsumptionLast7Days() {
        return Transformations.switchMap(dadosUsuario, dados -> {
            if (dados == null) {
                return repository.getWaterConsumptionData(null);
            }
            return repository.getWaterConsumptionData(dados);
        });
    }

    public LiveData<Map<String, Map<String, Integer>>> getSleepDataLast7Days() {
        return Transformations.switchMap(dadosUsuario, dados -> {
            if (dados == null) {
                return repository.getSleepData(null);
            }
            return repository.getSleepData(dados);
        });
    }

    public LiveData<List<TreinoReportItem>> getTrainingReportData() {
        return Transformations.switchMap(dadosUsuario, dados -> {
            if (dados == null) {
                return repository.getTrainingReportData(null);
            }
            return repository.getTrainingReportData(dados);
        });
    }

    public LiveData<Map<String, Map<String, Integer>>> getMoodLevelsLast7Days() {
        return Transformations.switchMap(dadosUsuario, dados -> {
            if (dados == null) {
                return repository.getMoodLevelsData(null);
            }
            return repository.getMoodLevelsData(dados);
        });
    }

    // --- LiveData para a Tabela de Dores ---
    public LiveData<List<PainReportItem>> getPainReportData() {
        return Transformations.switchMap(dadosUsuario, dados -> {
            if (dados == null) {
                return repository.getPainReportData(null);
            }
            return repository.getPainReportData(dados);
        });
    }

}

