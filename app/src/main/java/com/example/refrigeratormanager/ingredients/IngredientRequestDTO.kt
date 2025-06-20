package com.example.refrigeratormanager.ingredients

data class IngredientRequestDTO(
    val refrigeratorId: Int,
    val ingredientsName: String,
    val quantity: Int,
    val expirationDate: String,
    val storageLocation: Int,
)
