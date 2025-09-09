package com.mysite.ref.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeOrderRepository extends JpaRepository<RecipeOrder, Integer> {
    List<RecipeOrder> findByRecipe_RecipeIdOrderByStepNumberAsc(String recipeId);
}


