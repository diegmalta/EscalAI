package com.ufrj.escalaiv2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.ufrj.escalaiv2.dao.UserDailyDataDao;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.enums.HumorValues;
import com.ufrj.escalaiv2.model.UserDailyData;
import com.ufrj.escalaiv2.enums.TipoTreino; // Importar TipoTreino

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserDailyDataRepository {
    private final UserDailyDataDao userDailyDataDao;
    private final String today;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public UserDailyDataRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDailyDataDao = database.userDailyDataDao();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public LiveData<List<UserDailyData>> getDailyDataBetweenDatesLiveData(int userId, String startDate, String endDate) {
        return userDailyDataDao.getDailyDataBetweenDates(userId, startDate, endDate);
    }

    public boolean saveMoodData(int userId, HumorValues joyLevel, HumorValues sadnessLevel,
                                HumorValues anxietyLevel, HumorValues stressLevel, HumorValues calmLevel) {
        AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        databaseWriteExecutor.execute(() -> {
            try {
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                UserDailyData userDailyData = userDailyDataDao.getUserDailyData(userId, todayDate);

                if (userDailyData == null) {
                    userDailyData = new UserDailyData(userId, todayDate);
                }

                userDailyData.setJoyLevel(joyLevel.ordinal());
                userDailyData.setSadnessLevel(sadnessLevel.ordinal());
                userDailyData.setAnxietyLevel(anxietyLevel.ordinal());
                userDailyData.setStressLevel(stressLevel.ordinal());
                userDailyData.setCalmLevel(calmLevel.ordinal());

                if (userDailyData.getId() == 0) {
                    long id = userDailyDataDao.insert(userDailyData);
                    success.set(id > 0);
                } else {
                    userDailyDataDao.update(userDailyData);
                    success.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                success.set(false);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return success.get();
    }

    // Busca ou cria o registro do dia
    private UserDailyData getOrCreateUserDailyData(int userId, String date) {
        UserDailyData data = userDailyDataDao.getUserDailyData(userId, date);
        if (data == null) {
            data = new UserDailyData(userId, date);
            // Não insere aqui, a inserção/update acontece no método que chama este
        }
        if (data.getTreinosMap() == null) {
            data.setTreinosMap(new HashMap<>());
        }
        return data;
    }

    // Método para adicionar consumo de água
    public void addWaterConsumption(int userId, int amount) {
        databaseWriteExecutor.execute(() -> {
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            UserDailyData data = getOrCreateUserDailyData(userId, todayDate);
            data.setWaterConsumed(data.getWaterConsumed() + amount);
            if (data.getId() == 0) {
                userDailyDataDao.insert(data);
            } else {
                userDailyDataDao.update(data);
            }
        });
    }

    // Obter total de água consumida hoje (síncrono, cuidado na UI thread)
    public int getTotalWaterConsumption(int userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        UserDailyData userDailyData = userDailyDataDao.getUserDailyData(userId, todayDate);
        return userDailyData != null ? userDailyData.getWaterConsumed() : 0;
    }

    public LiveData<Integer> getTotalWaterConsumptionLiveData(int userId, String date) {
        return userDailyDataDao.getTotalWaterConsumptionLiveData(userId, date);
    }

    public UserDailyData getTodayMoodData(int userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return userDailyDataDao.getUserDailyData(userId, todayDate);
    }

    public void resetWaterConsumption(int userId) {
        databaseWriteExecutor.execute(() -> {
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            UserDailyData data = userDailyDataDao.getUserDailyData(userId, todayDate);
            if (data != null) {
                data.setWaterConsumed(0);
                userDailyDataDao.update(data);
            }
        });
    }

    public boolean saveDorData(int userId, int areaDor, int subareaDor, int especificacaoDor, int intensidadeDor) {
        AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        databaseWriteExecutor.execute(() -> {
            try {
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                UserDailyData userDailyData = getOrCreateUserDailyData(userId, todayDate);

                userDailyData.setAreaDorN1(areaDor);
                userDailyData.setAreaDorN2(subareaDor);
                userDailyData.setAreaDorN3(especificacaoDor);
                userDailyData.setIntensidadeDor(intensidadeDor);

                if (userDailyData.getId() == 0) {
                    long id = userDailyDataDao.insert(userDailyData);
                    success.set(id > 0);
                } else {
                    userDailyDataDao.update(userDailyData);
                    success.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                success.set(false);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return success.get();
    }

    public UserDailyData getTodayDorData(int userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return userDailyDataDao.getUserDailyData(userId, todayDate);
    }

    public boolean saveSleepData(int userId, String date, String sleepTime, String wakeTime, Integer totalSleepMinutes, Integer quality) {
        AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        databaseWriteExecutor.execute(() -> {
            try {
                UserDailyData userDailyData = getOrCreateUserDailyData(userId, date);

                userDailyData.setSleepTime(sleepTime);
                userDailyData.setWakeTime(wakeTime);
                userDailyData.setTotalSleepTimeInMinutes(totalSleepMinutes != null ? totalSleepMinutes : 0);
                userDailyData.setSleepQuality(quality != null ? quality : 3);

                if (userDailyData.getId() == 0) {
                    long id = userDailyDataDao.insert(userDailyData);
                    success.set(id > 0);
                } else {
                    userDailyDataDao.update(userDailyData);
                    success.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                success.set(false);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return success.get();
    }

    // --- Métodos de Treino Atualizados ---

    /**
     * Salva ou atualiza os dados de um treino para o usuário no dia atual.
     * Adiciona a duração ao tipo de treino especificado no mapa de treinos do dia.
     *
     * @param userId ID do usuário.
     * @param tipoTreinoIndex Índice do TipoTreino (enum ordinal).
     * @param duracaoMinutos Duração do treino em minutos.
     * @return true se o salvamento foi bem-sucedido, false caso contrário.
     */
    public boolean saveTreinoData(int userId, int tipoTreinoIndex, int duracaoMinutos) {
        AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Iniciando salvamento: userId=" + userId + 
                ", tipoIndex=" + tipoTreinoIndex + ", duracao=" + duracaoMinutos);

        databaseWriteExecutor.execute(() -> {
            try {
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Data de hoje: " + todayDate);
                UserDailyData userDailyData = getOrCreateUserDailyData(userId, todayDate);
                android.util.Log.d("UserDailyDataRepo", "saveTreinoData - UserDailyData ID: " + userDailyData.getId());

                // Obter o nome/chave do tipo de treino a partir do índice
                TipoTreino tipoTreino = TipoTreino.getById(tipoTreinoIndex);
                if (tipoTreino == null) {
                    android.util.Log.e("UserDailyDataRepo", "Índice de tipo de treino inválido: " + tipoTreinoIndex);
                    throw new IllegalArgumentException("Índice de tipo de treino inválido: " + tipoTreinoIndex);
                }
                String treinoKey = tipoTreino.name();
                android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Tipo de treino: " + treinoKey);

                // Obter o mapa atual de treinos (agora sempre será um HashMap modificável)
                Map<String, Integer> treinosMap = userDailyData.getTreinosMap();
                android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Mapa antes: " + treinosMap);

                // Adicionar a nova duração à duração existente para este tipo de treino
                int duracaoAtual = treinosMap.getOrDefault(treinoKey, 0);
                treinosMap.put(treinoKey, duracaoAtual + duracaoMinutos);
                android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Mapa depois: " + treinosMap);

                // Atualizar o mapa na entidade (necessário por causa do TypeConverter)
                userDailyData.setTreinosMap(treinosMap);

                // Salvar (inserir ou atualizar) a entidade UserDailyData
                if (userDailyData.getId() == 0) {
                    android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Inserindo novo registro");
                    long id = userDailyDataDao.insert(userDailyData);
                    success.set(id > 0);
                    android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Inserção: " + (id > 0 ? "SUCESSO (ID=" + id + ")" : "FALHA"));
                } else {
                    android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Atualizando registro existente (ID=" + userDailyData.getId() + ")");
                    userDailyDataDao.update(userDailyData);
                    success.set(true);
                    android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Atualização: SUCESSO");
                }

            } catch (Exception e) {
                android.util.Log.e("UserDailyDataRepo", "saveTreinoData - ERRO ao salvar", e);
                success.set(false);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            android.util.Log.e("UserDailyDataRepo", "saveTreinoData - Thread interrompida", e);
        }
        android.util.Log.d("UserDailyDataRepo", "saveTreinoData - Resultado final: " + (success.get() ? "SUCESSO" : "FALHA"));
        return success.get();
    }
    public Map<String, Float> getTodayTrainingData(int userId) {
        Map<String, Float> resumoTreinosHoras = new HashMap<>();
        try {
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            android.util.Log.d("UserDailyDataRepo", "getTodayTrainingData - userId=" + userId + ", data=" + todayDate);
            UserDailyData userDailyData = userDailyDataDao.getUserDailyData(userId, todayDate);

            if (userDailyData != null && userDailyData.getTreinosMap() != null) {
                Map<String, Integer> treinosMapMinutos = userDailyData.getTreinosMap();
                android.util.Log.d("UserDailyDataRepo", "Treinos no Room (em minutos): " + treinosMapMinutos);

                for (Map.Entry<String, Integer> entry : treinosMapMinutos.entrySet()) {
                    String treinoKey = entry.getKey();
                    Integer duracaoMinutos = entry.getValue();

                    if (duracaoMinutos != null && duracaoMinutos > 0) {
                        // Tentar converter a chave (nome do enum) de volta para TipoTreino para obter o nome amigável
                        String nomeTreino;
                        try {
                            TipoTreino tipo = TipoTreino.valueOf(treinoKey);
                            nomeTreino = tipo.getNome();
                            android.util.Log.d("UserDailyDataRepo", "Convertido: " + treinoKey + " -> " + nomeTreino);
                        } catch (IllegalArgumentException e) {
                            // Se a chave não for um nome de enum válido (caso raro), usar a própria chave
                            nomeTreino = treinoKey;
                            android.util.Log.w("UserDailyDataRepo", "Não conseguiu converter: " + treinoKey);
                        }
                        float horasTreino = duracaoMinutos / 60.0f;
                        resumoTreinosHoras.put(nomeTreino, horasTreino);
                        android.util.Log.d("UserDailyDataRepo", "Adicionado ao resumo: " + nomeTreino + " = " + horasTreino + "h");
                    }
                }
            } else {
                android.util.Log.w("UserDailyDataRepo", "UserDailyData é null ou treinosMap é null");
            }
            android.util.Log.d("UserDailyDataRepo", "Resumo final (em horas): " + resumoTreinosHoras);
        } catch (Exception e) {
            android.util.Log.e("UserDailyDataRepo", "Erro ao buscar treinos de hoje", e);
        }
        return resumoTreinosHoras;
    }
//    public LiveData<List<UserDailyData>> getUserDailyDataLiveData(int userId, String date) {
//        return userDailyDataDao.getUserDailyDataLiveData(userId, date);
//    }
}

