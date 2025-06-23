package com.example.refrigeratormanager.ingredients

import com.example.refrigeratormanager.DTO.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PATCH("ingredients/{ingredientsId}/note")
    fun updateNote(
        @Header("Authorization") token: String,
        @Path("ingredientsId") ingredientsId: Int,
        @Body noteUpdateRequestDTO: NoteUpdateRequestDTO
    ): Call<ApiResponse>

    @DELETE("ingredients/{ingredientsId}")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("ingredientsId") ingredientsId: Int
    ): Call<ApiResponse>

    @PUT("ingredients/update")
    fun updateIngredient(
        @Header("Authorization") token: String,
        @Body request: IngredientUpdateRequestDTO
    ): Call<Void>

}