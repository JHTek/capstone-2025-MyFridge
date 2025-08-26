package com.example.refrigeratormanager

import com.example.refrigeratormanager.product.Product
import retrofit2.http.GET
import retrofit2.http.Query

interface ExpirationApi {
    // suspend 함수로 변경
    // 서버 엔드포인트 URL 확인 후 "products/expiring" 맞게 조정 필요
    @GET("products/expiring")
    suspend fun getExpiringProducts(
        @Query("userId") userId: String,
        @Query("days") alertDays: Int
    ): List<Product>
}
