package com.ufrj.escalaiv2.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ufrj.escalaiv2.dao.UsuarioDao;
import com.ufrj.escalaiv2.dao.UserDailyDataDao;


@Database(
        entities = {Usuario.class, UserDailyData.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();
    public abstract UserDailyDataDao userDailyDataDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "escalaiv2_database"
                    )
                    .allowMainThreadQueries() // Only for testing, remove in production
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}