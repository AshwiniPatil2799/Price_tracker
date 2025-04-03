package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ImageView biometric;
    private PriceTrackerViewModel viewModel;
    private ConstraintLayout layout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IntiView();
        authbiometric();;


        // Login button
        loginButton.setOnClickListener(v -> authenticateUser());
    }

    private void IntiView(){
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        biometric = findViewById(R.id.biometricimg);
        layout=findViewById(R.id.Layout);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PriceTrackerViewModel.class);

    }


    private void authbiometric (){

        // Check if a saved token exists
        String token = SharedPrefUtils.getToken(this);
        if (token != null && BiometricUtil.isBiometricAvailable(this)) {
            layout.setVisibility(View.VISIBLE);
            biometric.setVisibility(View.VISIBLE);

            authenticateBiometrically();
            usernameEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
        } else {
            biometric.setVisibility(View.GONE);
        }

    }
    private void authenticateUser() {
        //  input values
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // API call for login
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        String token = response.body().getToken();
                        SharedPrefUtils.saveToken(LoginActivity.this, token);
                        fetchTransactions(token);
                        navigateToTransactionsScreen();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTransactions(String token) {
        // Add "Bearer "  token
        String authToken = "Bearer " + token;

        // API call to fetch transactions data
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<PriceTrackerEntity>> call = apiService.getTransactions(authToken);

        call.enqueue(new Callback<List<PriceTrackerEntity>>() {
            @Override
            public void onResponse(Call<List<PriceTrackerEntity>> call, Response<List<PriceTrackerEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PriceTrackerEntity> transactions = response.body();
                    viewModel.insertTransactions(transactions); // Save to Room DB
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to fetch transactions.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PriceTrackerEntity>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateBiometrically() {
        BiometricUtil.authenticateUser(this, new BiometricUtil.BiometricCallback() {
            @Override
            public void onAuthenticationSuccess() {
                Toast.makeText(LoginActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                navigateToTransactionsScreen();
            }

            @Override
            public void onAuthenticationFailure() {
                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(String error) {
                Toast.makeText(LoginActivity.this, "Authentication Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToTransactionsScreen() {
        Intent intent = new Intent(this, PricesActivity.class);
        startActivity(intent);
        finish();
    }
}