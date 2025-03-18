package com.ufrj.escalaiv2.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ufrj.escalaiv2.dao.UsuarioDao;

@Database(
        entities = {Usuario.class},
        version = 1,
        exportSchema = false // Add this to prevent schema export warnings
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();

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