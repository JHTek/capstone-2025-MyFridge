package com.mysite.ref.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

import com.mysite.ref.dto.RecipeResponse;

@Data
@AllArgsConstructor
public class IngredientSectionResponse {
    private String ingredientName;
    private List<RecipeResponse> recipes;
}
