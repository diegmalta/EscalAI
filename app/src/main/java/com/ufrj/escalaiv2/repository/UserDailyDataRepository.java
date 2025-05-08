package com.ufrj.escalaiv2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.ufrj.escalaiv2.dao.UserDailyDataDao;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.enums.HumorValues;
import com.ufrj.escalaiv2.model.UserDailyData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UserDailyDataRepository {
    private final UserDailyDataDao userDailyDataDao;
    private final String today;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public UserDailyDataRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDailyDataDao = database.userDailyDataDao();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Método para salvar ou atualizar dados de humor
    public boolean saveMoodData(int userId, HumorValues joyLevel, HumorValues sadnessLevel,
                                HumorValues anxietyLevel, HumorValues stressLevel, HumorValues calmLevel) {
        try {
            // Usando AtomicBoolean para retornar valor de thread secundária
            AtomicBoolean success = new AtomicBoolean(false);
            // Usando CountDownLatch para aguardar execução
            CountDownLatch latch = new CountDownLatch(1);

            databaseWriteExecutor.execute(() -> {
                try {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    UserDailyData userDailyData = userDailyDataDao.getUserDailyDataForToday(userId, today);

                    if (userDailyData == null) {
                        userDailyData = new UserDailyData(userId, today);
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

            // Esperar pela conclusão (cuidado com ANR)
            latch.await(2, TimeUnit.SECONDS);
            return success.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public UserDailyData getUserDailyData(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        final UserDailyData userDailyData = userDailyDataDao.getUserDailyDataForToday(userId, today);

        if (userDailyData == null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                userDailyDataDao.insert(userDailyData);
            });
        }

        return userDailyData;
    }

    // Método para adicionar consumo de água
    public void addWaterConsumption(int userId, int amount) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseWriteExecutor.execute(() -> {
            UserDailyData data = userDailyDataDao.getUserDailyDataForToday(userId, today);
            if (data == null) {
                data = new UserDailyData(userId, today);
                data.setUserId(userId);
                data.setDate(today);
                data.setWaterConsumed(amount);
                userDailyDataDao.insert(data);
            } else {
                data.setWaterConsumed(data.getWaterConsumed() + amount);
                userDailyDataDao.update(data);
            }
        });
    }
//    public boolean addWaterConsumption(int userId, int waterAmount) {
//        try {
//            // Verificar se já existe um registro para hoje
//            UserDailyData userDailyData = userDailyDataDao.getUserDailyDataForToday(userId);
//
//            if (userDailyData == null) {
//                // Criar novo registro se não existir
//                userDailyData = new UserDailyData();
//                userDailyData.setUserId(userId);
//                userDailyData.setDate(today);
//                // Valores padrão para humor usando a enumeração (MODERATE é o valor 2)
//                userDailyData.setJoyLevel(HumorValues.MODERATE.ordinal());
//                userDailyData.setSadnessLevel(HumorValues.MODERATE.ordinal());
//                userDailyData.setAnxietyLevel(HumorValues.MODERATE.ordinal());
//                userDailyData.setStressLevel(HumorValues.MODERATE.ordinal());
//                userDailyData.setCalmLevel(HumorValues.MODERATE.ordinal());
//                userDailyData.setWaterConsumed(waterAmount);
//
//                long id = userDailyDataDao.insert(userDailyData);
//                return id > 0;
//            } else {
//                // Atualizar consumo de água
//                int currentWater = userDailyData.getWaterConsumed();
//                userDailyData.setWaterConsumed(currentWater + waterAmount);
//                userDailyDataDao.update(userDailyData);
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    // Obter total de água consumida hoje
    public int getTotalWaterConsumption(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        UserDailyData userDailyData = userDailyDataDao.getUserDailyDataForToday(userId, today);
        return userDailyData != null ? userDailyData.getWaterConsumed() : 0;
    }

    public LiveData<Integer> getTotalWaterConsumptionLiveData(int userId, String date) {
        return userDailyDataDao.getTotalWaterConsumptionLiveData(userId, date);
    }

    // Obter dados de humor do dia atual
    public UserDailyData getTodayMoodData(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return userDailyDataDao.getUserDailyDataForToday(userId, today);
    }

    // Resetar consumo de água para o dia atual
    public void resetWaterConsumption(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseWriteExecutor.execute(() -> {
            UserDailyData data = userDailyDataDao.getUserDailyDataForToday(userId, today);
            if (data != null) {
                data.setWaterConsumed(0);
                userDailyDataDao.update(data);
            }
        });
    }

    public boolean saveDorData(int userId, int areaDor, int subareaDor, int especificacaoDor, int intensidadeDor) {
        try {
            // Usando AtomicBoolean para retornar valor de thread secundária
            AtomicBoolean success = new AtomicBoolean(false);
            // Usando CountDownLatch para aguardar execução
            CountDownLatch latch = new CountDownLatch(1);

            databaseWriteExecutor.execute(() -> {
                try {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    UserDailyData userDailyData = userDailyDataDao.getUserDailyDataForToday(userId, today);

                    if (userDailyData == null) {
                        userDailyData = new UserDailyData(userId, today);
                    }

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
            latch.await(2, TimeUnit.SECONDS);
            return success.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserDailyData getTodayDorData(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return userDailyDataDao.getUserDailyDataForToday(userId, today);
    }
}