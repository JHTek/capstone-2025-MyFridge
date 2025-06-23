package com.example.refrigeratormanager.recipe

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Path

interface RecipeApi {
    @GET("recipes/search")
    fun searchRecipes(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String
    ): Call<List<Recipe>>

    @GET("/recipes/recommend/user")
    fun getRecommendedRecipes(
        @Header("Authorization") token: String
    ): Call<List<IngredientSection>>

}