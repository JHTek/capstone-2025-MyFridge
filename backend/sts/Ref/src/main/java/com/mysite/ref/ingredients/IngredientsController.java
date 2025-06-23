package com.mysite.ref.ingredients;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.ref.dto.ApiResponse;
import com.mysite.ref.dto.IngredientRequestDTO;
import com.mysite.ref.dto.IngredientResponseDTO;
import com.mysite.ref.dto.IngredientUpdateRequestDTO;
import com.mysite.ref.dto.NoteUpdateRequestDTO;
import com.mysite.ref.refrigerator.RefrigeratorService;
import com.mysite.ref.user.JWTUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientsController {
	
	private final IngredientsService ingredientsService;
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addIngredients(@RequestBody List<IngredientRequestDTO> ingredientsDtoList){
		ingredientsService.addIngredients(ingredientsDtoList);
		 ApiResponse response = new ApiResponse("Ingredients added successfully");
		    return ResponseEntity.ok(response);
    }
	
	
	@GetMapping("/refrigerator/{refrigeratorId}")
	public ResponseEntity<List<IngredientResponseDTO>> getIngredientsByRefrigeratorId(@PathVariable("refrigeratorId") int refrigeratorId) {
		List<IngredientResponseDTO> ingredients = ingredientsService.getIngredientsByRefrigeratorId(refrigeratorId);
		return ResponseEntity.ok(ingredients);
	}
	
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateIngredient(@RequestBody IngredientUpdateRequestDTO dto) {
	    ingredientsService.updateIngredient(dto);
	    return ResponseEntity.ok(new ApiResponse("재료가 성공적으로 수정되었습니다."));
	}
	@PatchMapping("/{ingredientsId}/note")
	public ResponseEntity<ApiResponse> updateNote(
	    @PathVariable("ingredientsId") int ingredientsId,
	    @RequestBody NoteUpdateRequestDTO request
	) {
	    ingredientsService.updateNote(ingredientsId, request.getNote());
	    return ResponseEntity.ok(new ApiResponse("비고가 성공적으로 수정되었습니다."));
	}
	
	@DeleteMapping("/delete/{ingredientsId}")
	public ResponseEntity<ApiResponse> deleteIngredient(@PathVariable("ingredientsId") int ingredientsId) {
	    ingredientsService.deleteIngredient(ingredientsId);
	    return ResponseEntity.ok(new ApiResponse("재료가 성공적으로 삭제되었습니다."));
	}


}

