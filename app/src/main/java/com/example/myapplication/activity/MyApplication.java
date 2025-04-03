package com.example.myapplication.activity;
import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;

public class MyApplication extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Room database
        SharedPreferences preferences = getSharedPreferences(SharedPrefUtils.PREFS_FILE, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("DarkMode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}