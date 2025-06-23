package com.mysite.ref.recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mysite.ref.dto.RecipeResponse;
import com.mysite.ref.ingredients.ClassRepository;
import com.mysite.ref.ingredients.Ingredients;
import com.mysite.ref.ingredients.IngredientsRepository;
import com.mysite.ref.refrigerator.Refrigerator;
import com.mysite.ref.refrigerator.UserRefCRepository;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientsRepository ingredientsRepository;
    private final ClassRepository classRepository;
    private final RecipeClassRepository recipeClassRepository;
    private final UserRefCRepository userRefCRepository;

    
    // 1. 유통기한이 적게 남은 재료를 사용하는 레시피 추천
    public List<Recipe> recommendRecipes(Integer refrigeratorId) {
        // 유통기한이 임박한 재료 5개 가져오기 (3일 이내)
    	Page<Ingredients> expiringIngredients = ingredientsRepository
                .findByRefrigeratorRefrigeratorIdAndExpirationDateBetweenOrderByExpirationDateAsc(
                    refrigeratorId,
                    LocalDate.now(),
                    LocalDate.now().plusDays(3),
                    PageRequest.of(0, 10) // 상위 10개 재료만 조회
                );
        
    	List<Integer> classIds = expiringIngredients.stream()
                .map(i -> i.getClassEntity().getClassId())
                .distinct()
                .collect(Collectors.toList());

            if (classIds.isEmpty()) {
                return Collections.emptyList();
            }
        
            
        // 해당 class들을 사용하는 레시피 조회
            return recipeRepository.findByClassIds(classIds)
                    .stream()
                    .limit(5) // 상위 5개만 선택
                    .collect(Collectors.toList());
            }
    
 // 유저의 모든 냉장고 가져오기
    public List<IngredientSectionResponse> recommendRecipesByUser(String userId) {

        List<Refrigerator> refrigerators = userRefCRepository.findRefrigeratorsByUserId(userId); 
        if (refrigerators.isEmpty()) {
            return Collections.emptyList();
        }

        // 유통기한 임박 재료 모두 모으기
        List<Ingredients> allExpiringIngredients = refrigerators.stream()
            .flatMap(ref -> ingredientsRepository
                .findByRefrigeratorRefrigeratorIdAndExpirationDateBetweenOrderByExpirationDateAsc(
                    ref.getRefrigeratorId(),
                    LocalDate.now(),
                    LocalDate.now().plusDays(3),
                    PageRequest.of(0, 10)
                ).stream())
            .collect(Collectors.toList());

        // 재료명 기준으로 그룹핑
        Map<String, List<Ingredients>> groupedByIngredientName = allExpiringIngredients.stream()
        		.collect(Collectors.groupingBy(Ingredients::getIngredientsName));


        List<IngredientSectionResponse> result = new ArrayList<>();

        for (Map.Entry<String, List<Ingredients>> entry : groupedByIngredientName.entrySet()) {
            String ingredientName = entry.getKey();

            // 해당 재료를 사용하는 레시피 최대 2개 찾기
            List<Recipe> matchedRecipes = recipeRepository.findByIngredientName(ingredientName, PageRequest.of(0, 2));

            if (!matchedRecipes.isEmpty()) {
                List<RecipeResponse> recipeResponses = matchedRecipes.stream()
                    .map(RecipeResponse::from)
                    .collect(Collectors.toList());

                result.add(new IngredientSectionResponse(ingredientName, recipeResponses));
            }

            // 최대 5섹션까지만
            if (result.size() >= 5) break;
        }

        return result;
    }


    
    
    // 2. 레시피 이름으로 검색
	public List<Recipe> searchRecipes(String keyword) {
		return recipeRepository.findByRecipeNameContainingIgnoreCase(keyword);
}
    
    // 3. 레시피 상세 정보 조회
	public Recipe getRecipeDetail(String recipeId) {
		
	    return recipeRepository.findWithIngredientsById(recipeId)
	        .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));
	}
	
	

}