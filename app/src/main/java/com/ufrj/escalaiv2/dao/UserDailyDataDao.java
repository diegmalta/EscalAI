package com.ufrj.escalaiv2.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ufrj.escalaiv2.model.UserDailyData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Dao
public interface UserDailyDataDao {
    @Insert
    long insert(UserDailyData userDailyData);

    @Update
    void update(UserDailyData userDailyData);

    @Query("SELECT * FROM users_daily_data WHERE userId = :userId AND date = :today")
    UserDailyData getUserDailyDataForToday(int userId, String today);

    // Overloaded method for convenience
    default UserDailyData getUserDailyDataForToday(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return getUserDailyDataForToday(userId, today);
    }
}