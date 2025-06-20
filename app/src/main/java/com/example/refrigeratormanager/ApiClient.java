package com.example.refrigeratormanager;

import android.util.Log;

import com.example.refrigeratormanager.ingredients.IngredientApi;
import com.example.refrigeratormanager.recipe.RecipeApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Log.d("API_CLIENT", "SERVER_IP from BuildConfig: " + BuildConfig.SERVER_IP);
        if (BuildConfig.SERVER_IP == null || BuildConfig.SERVER_IP.isEmpty()) {
            throw new IllegalStateException("❗ SERVER_IP가 설정되지 않았습니다. local.properties에 SERVER_IP=10.0.2.2 같은 형식으로 넣어주세요.");
        }

        String BASE_URL = "http://" + BuildConfig.SERVER_IP + ":8080/";

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static RefrigeratorApi getRefrigeratorApi() {
        return getClient().create(RefrigeratorApi.class);
    }
    public static IngredientApi getIngredientApi() {
        return getClient().create(IngredientApi.class);
    }
    public static RecipeApi getRecipeApi() {
        return getClient().create(RecipeApi.class);
    }
}
