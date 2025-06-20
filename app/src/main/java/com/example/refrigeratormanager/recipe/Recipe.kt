package com.example.refrigeratormanager.recipe

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("recipeId") val id: String,
    @SerializedName("recipeName") val recipe_name: String,  // ✅ JSON의 "recipeName" → name으로 매핑
    val thumbnail: String,
    val url: String?,
    val ingredients: List<Ingredient>
)

data class IngredientSection(
    val ingredientName: String,
    val recipes: List<Recipe>
)

data class Ingredient(
    @SerializedName("name") val ingre_name: String,
)
