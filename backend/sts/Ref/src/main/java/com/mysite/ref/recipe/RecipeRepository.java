package com.mysite.ref.recipe;

import java.util.List;

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
	
}
