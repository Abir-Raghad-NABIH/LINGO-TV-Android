package com.example.moviesapp_nabih.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LingoRetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static LingoRetrofitClient instance;
    private Retrofit retrofit;

    private LingoRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized LingoRetrofitClient getInstance() {
        if (instance == null) {
            instance = new LingoRetrofitClient();
        }
        return instance;
    }

    public LingoApiService getApiService() {
        return retrofit.create(LingoApiService.class);
    }
}