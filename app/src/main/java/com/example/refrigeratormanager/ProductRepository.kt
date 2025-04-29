package com.example.refrigeratormanager

import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.api.IngredientApi
import com.example.refrigeratormanager.IngredientResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ProductRepository {

    fun fetchProducts(
        token: String,
        refrigeratorId: Int,
        onSuccess: (List<IngredientResponseDTO>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val api = ApiClient.getIngredientApi()
        api.getIngredientsByRefrigeratorId("Bearer $token", refrigeratorId)
            .enqueue(object : Callback<List<IngredientResponseDTO>> {
                override fun onResponse(
                    call: Call<List<IngredientResponseDTO>>,
                    response: Response<List<IngredientResponseDTO>>
                ) {
                    if (response.isSuccessful) {
                        onSuccess(response.body() ?: emptyList())
                    } else {
                        onFailure(Throwable("서버 오류: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<List<IngredientResponseDTO>>, t: Throwable) {
                    onFailure(t)
                }
            })
    }
}