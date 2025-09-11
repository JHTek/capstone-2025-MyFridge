package com.mysite.ref.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeOrderRepository extends JpaRepository<RecipeOrder, Integer> {
    List<RecipeOrder> findByRecipe_RecipeIdOrderByStepNumberAsc(String recipeId);
}


