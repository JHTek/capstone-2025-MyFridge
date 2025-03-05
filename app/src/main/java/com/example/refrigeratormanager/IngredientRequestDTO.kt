package com.example.refrigeratormanager

import org.threeten.bp.LocalDate

data class IngredientRequestDTO(
    val refrigeratorId: Int,
    val ingredientsName: String,
    val quantity: Int,
    val expirationDate: String,
    val storageLocation: Int,
)
