package com.ufrj.escalaiv2.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ufrj.escalaiv2.dao.UserDailyDataDao;
import com.ufrj.escalaiv2.model.AppDatabase;
import com.ufrj.escalaiv2.model.UserDailyData;

public class HumorRepository {
    private UserDailyDataDao userDailyDataDao;
    private final SharedPreferences preferences;

    public HumorRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDailyDataDao = database.userDailyDataDao();
        preferences = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    public void addWaterConsumption(int userId, int waterAmount) {
        UserDailyData existingData = userDailyDataDao.getUserDailyDataForToday(userId);

        if (existingData != null) {
            existingData.setWaterConsumed(existingData.getWaterConsumed() + waterAmount);
            userDailyDataDao.update(existingData);
        } else {
            UserDailyData dailyData = new UserDailyData();
            dailyData.setUserId(userId);
            dailyData.setWaterConsumed(waterAmount);
            userDailyDataDao.insert(dailyData);
        }
    }

    public int getTotalWaterConsumption(int userId) {
        UserDailyData dailyData = userDailyDataDao.getUserDailyDataForToday(userId);
        return dailyData != null ? dailyData.getWaterConsumed() : 0;
    }

    public void resetDailyWaterConsumption(int userId) {
        UserDailyData dailyData = userDailyDataDao.getUserDailyDataForToday(userId);
        if (dailyData != null) {
            dailyData.setWaterConsumed(0);
            userDailyDataDao.update(dailyData);
        }
    }
}