package com.mysite.ref.recipe;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, String> {
	
	// 레시피 이름으로 검색
	List<Recipe> findByRecipeNameContaining(String keyword);
	
    // 재료 종류로 레시피 검색
	@Query("SELECT DISTINCT r FROM Recipe r JOIN r.recipeClasses rc JOIN rc.classEntity c WHERE c.classId IN :classIds")
    List<Recipe> findByClassIds(@Param("classIds") List<Integer> classIds);
    
    List<Recipe> findByRecipeNameContainingIgnoreCase(String keyword);
    
 // 레시피 아이디로 재료 검색
    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.ingreLists WHERE r.recipeId = :recipeId")
    Optional<Recipe> findWithIngredientsById(@Param("recipeId") String recipeId);
    
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingreLists il WHERE il.ingreName = :ingredientName")
    List<Recipe> findByIngredientName(@Param("ingredientName") String ingredientName, Pageable pageable);

	
}
