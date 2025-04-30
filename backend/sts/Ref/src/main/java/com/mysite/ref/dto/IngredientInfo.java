package com.mysite.ref.dto;

import com.mysite.ref.recipe.IngreList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientInfo {
    private String name;
    private String count;
    private String unit;

    public static IngredientInfo from(IngreList ingreList) {
        return new IngredientInfo(
            ingreList.getIngreName(),
            ingreList.getIngreCount(),
            ingreList.getIngreUnit()
        );
    }
}
