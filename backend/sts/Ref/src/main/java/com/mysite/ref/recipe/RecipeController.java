package com.mysite.ref.recipe;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.ref.dto.RecipeResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    
    // 1. 추천 레시피
    @GetMapping("/recommend/{refrigeratorId}")
    public ResponseEntity<List<RecipeResponse>> getRecommendedRecipes(
            @PathVariable("refrigeratorId") Integer refrigeratorId) {
        List<RecipeResponse> responses = recipeService.recommendRecipes(refrigeratorId).stream()
            .map(RecipeResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    //레시피 검색
    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(
            @RequestParam("keyword") String keyword) {
        List<RecipeResponse> responses = recipeService.searchRecipes(keyword).stream()
            .map(RecipeResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    //레시피 상세정보
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> getRecipeDetail(
            @PathVariable("recipeId") String recipeId) {
        Recipe recipe = recipeService.getRecipeDetail(recipeId);
        return ResponseEntity.ok(RecipeResponse.from(recipe));
    }
}