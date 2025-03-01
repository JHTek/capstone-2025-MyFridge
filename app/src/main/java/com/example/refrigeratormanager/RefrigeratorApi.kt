package com.example.refrigeratormanager

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RefrigeratorApi {
    @POST("/refrigerator/create")
    fun createRefrigerator(
        @Header("Authorization") token: String,
        @Body refrigerator: RefrigeratorDTO
    ): Call<String>
}
