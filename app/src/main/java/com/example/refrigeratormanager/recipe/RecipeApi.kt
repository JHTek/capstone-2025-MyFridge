package com.example.refrigeratormanager.recipe

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Header

interface RecipeApi {
    @GET("recipes/search")
    fun searchRecipes(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String
    ): Call<List<Recipe>>
}