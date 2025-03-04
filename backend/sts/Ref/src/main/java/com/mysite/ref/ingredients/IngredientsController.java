package com.mysite.ref.ingredients;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.ref.dto.IngredientRequestDTO;
import com.mysite.ref.refrigerator.RefrigeratorService;
import com.mysite.ref.user.JWTUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientsController {
	
	private final IngredientsService ingredientsService;
	
	@PostMapping("/add")
	public ResponseEntity<String> addIngredients(@RequestBody List<IngredientRequestDTO> ingredientsDtoList){
		ingredientsService.addIngredients(ingredientsDtoList);
        return ResponseEntity.ok("Ingredients added successfully");
    }
}

