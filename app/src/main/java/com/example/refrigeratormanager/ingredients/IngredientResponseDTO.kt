package com.example.refrigeratormanager.ingredients

data class IngredientResponseDTO(
    val ingredientsId: Int,
    val refrigeratorId: Int,
    val ingredientsName: String,
    val quantity: Int,
    val expirationDate: String,
    val storageLocation: Int,
    val refrigeratorName: String,
    val category: String,
    val note: String?
)