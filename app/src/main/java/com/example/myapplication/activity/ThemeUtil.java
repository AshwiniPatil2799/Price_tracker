package com.example.myapplication.activity;
import android.app.Activity;
import android.content.SharedPreferences;

public class ThemeUtil {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_THEME = "theme";

    public static void setTheme(Activity activity, boolean darkMode) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_THEME, darkMode).apply();
    }

    public static boolean isDarkMode(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(KEY_THEME, false);
    }
}