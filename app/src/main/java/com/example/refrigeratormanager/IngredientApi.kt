package com.example.refrigeratormanager.api

import com.example.refrigeratormanager.DTO.ApiResponse
import com.example.refrigeratormanager.IngredientRequestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IngredientApi {
    @POST("/ingredients/add")
    fun uploadIngredients(
        @Header("Authorization") token: String,
        @Body ingredientRequests: List<IngredientRequestDTO>
    ): Call<ApiResponse>


}