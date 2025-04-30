package com.mysite.ref.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.mysite.ref.recipe.Recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeResponse {
    private String recipeId;
    private String recipeName;
    private String thumbnail;
    private String cookTime;
    private List<IngredientInfo> ingredients;

    public static RecipeResponse from(Recipe recipe) {
        List<IngredientInfo> ingredients = recipe.getIngreLists().stream()
            .map(IngredientInfo::from)
            .collect(Collectors.toList());

        return new RecipeResponse(
            recipe.getRecipeId(),
            recipe.getRecipeName(),
            recipe.getThumbnail(),
            recipe.getCookTime(),
            ingredients
        );
    }
}