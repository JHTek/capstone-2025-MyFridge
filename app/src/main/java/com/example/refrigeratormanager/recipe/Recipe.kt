package com.example.refrigeratormanager.recipe

data class Recipe(
    val id: String,
    val name: String,
    val thumbnail: String,
    val url: String,
    val ingredients: List<Ingredient>
)

data class IngredientSection(
    val ingredientName: String,
    val recipes: List<Recipe>
)

data class Ingredient(
    val ingre_name: String
)
