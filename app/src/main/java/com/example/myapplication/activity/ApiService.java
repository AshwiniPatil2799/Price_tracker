package com.example.myapplication.activity;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login") //use your api end point
    Call<LoginResponse> login(@Body LoginRequest loginRequest);


    @GET("prices")
    Call<List<PriceTrackerEntity>> getTransactions(@Header("Authorization") String token);
}