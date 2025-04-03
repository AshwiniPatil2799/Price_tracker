package com.example.myapplication.activity;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PriceTrackerEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PriceDao transactionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}