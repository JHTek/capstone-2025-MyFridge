package com.example.refrigeratormanager.recipe

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    @SerializedName("recipeId") val id: String,
    @SerializedName("recipeName") val recipeName: String,
    val thumbnail: String,
    @SerializedName("cookTime") val cookTime: String,
    val ingredients: List<Ingredient>,
    @SerializedName("recipeOrders") val instructions: List<Instruction> = emptyList()
) : Parcelable

data class IngredientSection(
    val ingredientName: String,
    val recipes: List<Recipe>
)

@Parcelize
data class Ingredient(
    @SerializedName("name") val name: String,
    @SerializedName("count") val count: String = "",
    @SerializedName("unit") val unit: String = ""
) : Parcelable

@Parcelize
data class Instruction(
    val instruction: String,
    val stepNumber: Int,
    val description: String,
    val photoUrl: String? // null 허용
) : Parcelable

