package com.example.refrigeratormanager

data class Recipe(
    val id: String,
    val name: String,
    val thumbnail: String,
    val url: String,
    val ingredients: List<String> // ingre_list의 ingre_name만 추출
)

data class IngredientSection(
    val ingredientName: String,
    val recipes: List<Recipe>
)


