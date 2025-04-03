package com.example.myapplication.activity;

import android.content.Context;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricUtil {

    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void authenticateUser(Context context, BiometricCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt.AuthenticationCallback biometricCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthenticationSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthenticationFailure();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onAuthenticationError(errString.toString());
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                (androidx.fragment.app.FragmentActivity) context,
                executor,
                biometricCallback
        );

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public interface BiometricCallback {
        void onAuthenticationSuccess();

        void onAuthenticationFailure();

        void onAuthenticationError(String error);
    }
}