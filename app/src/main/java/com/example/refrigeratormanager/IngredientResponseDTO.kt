package com.example.refrigeratormanager

data class IngredientResponseDTO(
    val ingredientsId: Int,
    val ingredientsName: String,
    val quantity: Int,
    val expirationDate: String,
    val storageLocation: Int,
    val refrigeratorName: String
)