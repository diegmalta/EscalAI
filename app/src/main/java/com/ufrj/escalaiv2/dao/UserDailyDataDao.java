package com.ufrj.escalaiv2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.ufrj.escalaiv2.model.UserDailyData;

@Dao
public interface UserDailyDataDao {
    @Insert
    long insert(UserDailyData userDailyData);

    @Update
    void update(UserDailyData userDailyData);

    @Query("SELECT * FROM user_daily_data WHERE userId = :userId AND date = :date")
    UserDailyData getUserDailyData(int userId, String date);

    @Query("SELECT * FROM user_daily_data WHERE userId = :userId AND date = :date")
    LiveData<UserDailyData> getUserDailyDataLiveData(int userId, String date);

    @Query("SELECT * FROM user_daily_data WHERE userId = :userId AND date = :today LIMIT 1")
    UserDailyData getUserDailyDataForToday(int userId, String today);

    @Query("SELECT water_consumed FROM user_daily_data WHERE userId = :userId AND date = :today")
    LiveData<Integer> getTotalWaterConsumptionLiveData(int userId, String today);

    @Query("SELECT * FROM user_daily_data WHERE userId = :userId AND date = :date")
    List<UserDailyData> getAllUserDailyDataForToday(int userId, String date);

    @Query("SELECT * FROM user_daily_data WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<UserDailyData>> getDailyDataBetweenDates(int userId, String startDate, String endDate);
}