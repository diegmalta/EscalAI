package com.ufrj.escalaiv2.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ufrj.escalaiv2.dao.UserDailyDataDao;
import com.ufrj.escalaiv2.dao.UsuarioDao;
import com.ufrj.escalaiv2.utils.DateTypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {Usuario.class, UserDailyData.class}, version = 4, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "escalai_database";
    private static AppDatabase instance;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract UsuarioDao usuarioDao();
    public abstract UserDailyDataDao userDailyDataDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}