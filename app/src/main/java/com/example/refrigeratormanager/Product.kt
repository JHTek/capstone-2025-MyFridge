package com.example.refrigeratormanager

data class Product(
    val refrigeratorId: Int,   // 냉장고 ID
    val ingredientsName: String,  // 제품 이름
    val quantity: Int,         // 수량
    val expirationDate: String,  // 유통기한
    val storageLocation: Int,   // 저장 위치 (냉장, 냉동, 실온)
    val category: String
)
