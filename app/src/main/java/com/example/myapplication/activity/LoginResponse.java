package com.example.myapplication.activity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }
    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }





}