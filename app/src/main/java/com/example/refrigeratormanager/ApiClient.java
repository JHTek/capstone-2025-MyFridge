package com.example.refrigeratormanager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()) // GsonConverterFactory 추가
                    .build();
        }
        return retrofit;
    }
    public static RefrigeratorApi getRefrigeratorApi() {
        return getClient().create(RefrigeratorApi.class);
    }
}