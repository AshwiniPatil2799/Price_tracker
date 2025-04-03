package com.example.myapplication.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PricesAdapter adapter;
    private LineChart chart;
    private List<PriceTrackerEntity> transactionList = new ArrayList<>();

    private PriceTrackerViewModel viewModel;
    private Map<Integer, Double> lastPrices = new HashMap<>();
    private SearchView searchEditText;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

      initializeUI();
      setupToolbar();
      setupRecyclerView();

createNotificationChannel();
        // Fetch prices from API
        fetchPrices();
    }
    private void initializeUI() {
        recyclerView = findViewById(R.id.Recyclerview);
        chart = findViewById(R.id.lineChart);

        searchEditText = findViewById(R.id.searchView);
        toolbar = findViewById(R.id.toolbar);
        viewModel = new ViewModelProvider(this).get(PriceTrackerViewModel.class);

        // Observe LiveData for transactions
        viewModel.getAllTransactions().observe(this, transactions -> {
            if (transactions != null) {
                adapter.updatePrices(transactions);
                updateChart(transactions);
            }
        });    }

    /**
     * Sets up the toolbar with back navigation.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Sets up RecyclerView and its adapter.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PricesAdapter();
        recyclerView.setAdapter(adapter);
    }

    /**
     * Data fetch
     */
    private void fetchPrices() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<PriceTrackerEntity>> call = apiService.getTransactions("Bearer YWRtaW46NjdlZDI1MjQ2NjE3NToxNzQzNTk0Nzg4");

        call.enqueue(new Callback<List<PriceTrackerEntity>>() {
            @Override
            public void onResponse(Call<List<PriceTrackerEntity>> call, Response<List<PriceTrackerEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PriceTrackerEntity> newPrices = response.body();

                    // Check for price changes and trigger notification
                    checkForPriceChange(newPrices);
                    // Update RecyclerView
                    adapter.updatePrices(newPrices);
                }
            }

            @Override
            public void onFailure(Call<List<PriceTrackerEntity>> call, Throwable throwable) {
                Log.e("API_ERROR", "Failed to fetch prices: " + throwable.getMessage());
            }
        });
    }


    private void checkForPriceChange(List<PriceTrackerEntity> newPrices) {
        Log.d("DEBUG", "Checking price changes...");

        if (lastPrices == null) {
            lastPrices = new HashMap<>();
        }

        for (PriceTrackerEntity price : newPrices) {
            if (!lastPrices.containsKey(price.getId())) {
                lastPrices.put(price.getId(), price.getPrice());  // Initialize first-time values
            }
        }

        for (PriceTrackerEntity price : newPrices) {
            int id = price.getId();
            double newPrice = price.getPrice();

            double oldPrice = lastPrices.get(id);
            Log.d("DEBUG", "Old Price: " + oldPrice + " | New Price: " + newPrice);

            if (oldPrice == newPrice) {
                Log.d("DEBUG", "Price changed for ID " + id + "!");
                sendNotification(price.getName(), oldPrice, newPrice);
            }

            lastPrices.put(id, newPrice);
        }
    }

    //MPAndroid chart
    private void updateChart(List<PriceTrackerEntity> prices) {
        if (prices == null || prices.isEmpty()) return;

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            entries.add(new Entry(i, (float) prices.get(i).getPrice()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price Trends");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);
        chart.invalidate(); // Refresh chart
    }

    /**
     * Notification Setup
     */

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "price_alert"; // 🔹 Must match the ID used when sending notification
            String channelName = "Price Alerts";
            String channelDescription = "Notifies when price changes";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Send Notification
    private void sendNotification(String name, double oldPrice, double newPrice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "price_alert")
                .setSmallIcon(R.drawable.baseline_notifications_none_24)
                .setContentTitle("Price Alert: " + name)
                .setContentText(name + " price changed: " + oldPrice + " → " + newPrice)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(), builder.build());

        Log.d("Note", "Notification sent");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted");
            } else {
                Log.d("Permission", "Notification permission denied");
                Toast.makeText(this, "Enable notifications in settings", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Sets up the ViewModel and observes database changes.
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PriceTrackerViewModel.class);

        // Observe transactions from the database
        viewModel.getAllTransactions().observe(this, transactions -> {
            transactionList.clear();
            transactionList.addAll(transactions);
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Sets up search functionality for filtering transactions.
     */
    private void setupSearchView() {
        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTransactions(newText);
                return true;
            }
        });
    }

    /**
     * Filters the transaction list based on the search text.
     *
     * @param text Search text entered by the user.
     */
    private void filterTransactions(String text) {
        ArrayList<PriceTrackerEntity> filteredList = new ArrayList<>();

        for (PriceTrackerEntity item : transactionList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getName().toLowerCase().contains(text.toLowerCase())  ) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);

        // Optionally, show a message if no data is found
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No matching transactions found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        } else if (id == R.id.action_dark_mode) {
            toggleDarkMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Shows a confirmation dialog for logout.
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Performs the logout operation.
     */
    private void performLogout() {
        // Clear saved token
        SharedPrefUtils.clearToken(this);

        // Navigate to Login Screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Toggles between dark and light mode.
     */
    private void toggleDarkMode() {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        SharedPreferences preferences = getSharedPreferences(SharedPrefUtils.PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("DarkMode", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("DarkMode", true);
        }

        editor.apply();
    }

}
