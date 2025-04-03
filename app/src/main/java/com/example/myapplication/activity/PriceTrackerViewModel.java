package com.example.myapplication.activity;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import java.util.List;
public class PriceTrackerViewModel extends AndroidViewModel {
    private final PriceDao priceDao;
    private final LiveData<List<PriceTrackerEntity>> allTransactions;

    public PriceTrackerViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        priceDao = database.transactionDao();


        allTransactions = priceDao.getAllTransactions();
    }

    public LiveData<List<PriceTrackerEntity>> getAllTransactions() {
        return allTransactions;
    }

    public void insertTransactions(List<PriceTrackerEntity> transactions) {
        new Thread(() -> priceDao.insertTransactions(transactions)).start();
    }
}