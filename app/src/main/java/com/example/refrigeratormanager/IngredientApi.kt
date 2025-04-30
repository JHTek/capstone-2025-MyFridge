package com.example.refrigeratormanager.api

import com.example.refrigeratormanager.DTO.ApiResponse
import com.example.refrigeratormanager.IngredientRequestDTO
import com.example.refrigeratormanager.IngredientResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface IngredientApi {
    @POST("ingredients/add")
    fun uploadIngredients(
        @Header("Authorization") token: String,
        @Body ingredientRequests: List<IngredientRequestDTO>
    ): Call<ApiResponse>

    @GET("ingredients/refrigerator/{refrigeratorId}")
    fun getIngredientsByRefrigeratorId(
        @Header("Authorization") token: String,
        @Path("refrigeratorId") refrigeratorId: Int
    ): Call<List<IngredientResponseDTO>>
}