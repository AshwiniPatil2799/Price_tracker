package com.example.myapplication.activity;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPrefUtils {
    public static final String PREFS_FILE = "secure_prefs";
    private static final String KEY_TOKEN = "token";


    private static SharedPreferences getEncryptedSharedPreferences(Context context) {
        try {
            String masterKeyAlias = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            }
            return EncryptedSharedPreferences.create(
                    PREFS_FILE,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to create encrypted SharedPreferences", e);
        }
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(context);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public static void clearToken(Context context) {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }
}