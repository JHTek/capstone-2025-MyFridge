package com.example.refrigeratormanager.product

import java.io.Serializable

data class Product(
    val ingredientsId: Int,
    val refrigeratorId: Int,   // 냉장고 ID
    val refrigeratorName: String, // 냉장고 이름
    val ingredientsName: String,  // 제품 이름
    val quantity: Int,         // 수량
    val expirationDate: String,  // 유통기한
    val storageLocation: Int,   // 저장 위치 (냉장, 냉동, 실온)
    val category: String,
    val note: String? = null
) : Serializable
