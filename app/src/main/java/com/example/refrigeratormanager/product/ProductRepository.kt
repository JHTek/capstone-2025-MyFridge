package com.example.refrigeratormanager.product

import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.DTO.ApiResponse
import com.example.refrigeratormanager.ingredients.IngredientResponseDTO
import com.example.refrigeratormanager.ingredients.IngredientUpdateRequestDTO
import com.example.refrigeratormanager.ingredients.NoteUpdateRequestDTO
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

    fun updateProductNote(
        token: String,
        productId: Int,
        note: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val api = ApiClient.getIngredientApi() // 이 부분 추가
        val request = NoteUpdateRequestDTO(note)
        val call = api.updateNote("Bearer $token", productId, request)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) onSuccess() else onFailure()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure()
            }
        })
    }

    fun deleteProduct(
        token: String,
        productId: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val api = ApiClient.getIngredientApi() // 이 부분 추가
        val call = api.deleteProduct("Bearer $token", productId)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) onSuccess() else onFailure()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure()
            }
        })
    }

    fun updateProduct(
        token: String,
        updatedProduct: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val request = IngredientUpdateRequestDTO(
            ingredientsId = updatedProduct.ingredientsId,
            ingredientsName = updatedProduct.ingredientsName,
            quantity = updatedProduct.quantity,
            expirationDate = updatedProduct.expirationDate,
            storageLocation = updatedProduct.storageLocation,
            category = updatedProduct.category,
            note = updatedProduct.note ?: ""
        )

        val api = ApiClient.getIngredientApi()
        api.updateIngredient("Bearer $token", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) onSuccess() else onFailure()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure()
            }
        })
    }


}