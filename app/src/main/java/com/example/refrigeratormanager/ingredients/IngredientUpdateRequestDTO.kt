package com.example.refrigeratormanager.ingredients

data class IngredientUpdateRequestDTO(
    val ingredientsId: Int,
    val ingredientsName: String,
    val quantity: Int,
    val expirationDate: String,
    val storageLocation: Int,
    val category: String,
    val note: String
)
