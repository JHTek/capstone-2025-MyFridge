package com.mysite.ref.recipe;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.ref.dto.RecipeResponse;
import org.springframework.data.domain.Pageable;
import com.mysite.ref.user.JWTUtil; 

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final JWTUtil jwtUtil;
    
    // 1. 추천 레시피
    @GetMapping("/recommend/{refrigeratorId}")
    public ResponseEntity<List<RecipeResponse>> getRecommendedRecipes(
            @PathVariable("refrigeratorId") Integer refrigeratorId) {
        List<RecipeResponse> responses = recipeService.recommendRecipes(refrigeratorId).stream()
            .map(RecipeResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/recommend/user")
    public ResponseEntity<List<IngredientSectionResponse>> getRecommendedRecipesByUser(
            @RequestHeader("Authorization") String token) {

        // 🔧 "Bearer " 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String userId = jwtUtil.getUserid(token);

        // 서비스에서 재료별 추천 레시피 섹션 반환
        List<IngredientSectionResponse> responses = recipeService.recommendRecipesByUser(userId);

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