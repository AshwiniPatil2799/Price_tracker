package com.example.myapplication.activity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PriceDao {
    @Query("SELECT * FROM transactions")
    LiveData<List<PriceTrackerEntity>> getAllTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransactions(List<PriceTrackerEntity> transactions);


}