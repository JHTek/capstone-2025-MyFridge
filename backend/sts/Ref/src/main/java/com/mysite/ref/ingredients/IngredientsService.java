package com.mysite.ref.ingredients;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.ref.dto.IngredientRequestDTO;
import com.mysite.ref.dto.IngredientResponseDTO;
import com.mysite.ref.dto.IngredientUpdateRequestDTO;
import com.mysite.ref.refrigerator.Refrigerator;
import com.mysite.ref.refrigerator.RefrigeratorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientsService {
	
	private final IngredientsRepository ingredientsRepository;
	private final RefrigeratorRepository refrigeratorRepository;
	private final ClassRepository classRepository;
	
	@Transactional
	 public void addIngredients(List<IngredientRequestDTO> ingredientsDtoList) {

        
		for (IngredientRequestDTO dto : ingredientsDtoList) {
            Refrigerator refrigerator = refrigeratorRepository.findById(dto.getRefrigeratorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid refrigerator ID"));
            
            String normalizedName = dto.getIngredientsName().trim().toLowerCase();
            ClassEntity classEntity = classRepository.findByClassNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    return classRepository.findById(1)
                        .orElseThrow(() -> new IllegalArgumentException("Default class not found"));
                });
            
            LocalDate expirationDate;
            if (dto.getExpirationDate() == null || dto.getExpirationDate().isEmpty()) {
                expirationDate = LocalDate.now().plusDays(classEntity.getShelfLife());
            } else {
                expirationDate = dto.getExpirationDateAsLocalDate();
            }

            Ingredients ingredient = new Ingredients();
            ingredient.setIngredientsName(dto.getIngredientsName());
            ingredient.setQuantity(dto.getQuantity());
            ingredient.setExpirationDate(dto.getExpirationDateAsLocalDate());
            ingredient.setExpirationDate(expirationDate);
            ingredient.setStorageLocation(dto.getStorageLocation());
            ingredient.setRefrigerator(refrigerator);
            ingredient.setClassEntity(classEntity); // 매핑된 클래스 설정
            ingredientsRepository.save(ingredient);
        }
    }
	
	@Transactional(readOnly = true)
	public List<IngredientResponseDTO> getIngredientsByRefrigeratorId(int refrigeratorId) {
		List<Ingredients> ingredients = ingredientsRepository.findByRefrigeratorRefrigeratorId(refrigeratorId);
		return ingredients.stream()
				.map(ingredient -> {
					IngredientResponseDTO dto = new IngredientResponseDTO();
					dto.setIngredientsId(ingredient.getIngredientsId());
					dto.setIngredientsName(ingredient.getIngredientsName());
					dto.setQuantity(ingredient.getQuantity());
					dto.setExpirationDate(ingredient.getExpirationDate());
					dto.setStorageLocation(ingredient.getStorageLocation());
					dto.setRefrigeratorName(ingredient.getRefrigerator().getRefrigeratorName()); // Refrigerator 엔티티의 이름을 가져옴
					dto.setNote(ingredient.getNote());
					if (ingredient.getClassEntity() != null) {
		                dto.setCategory(ingredient.getClassEntity().getCategory());
		            } else {
		                dto.setCategory("기타"); // 기본값 설정
		            }
		            
		            return dto;
				})
				.collect(Collectors.toList());
	}
	
	@Transactional
	public void updateIngredient(IngredientUpdateRequestDTO dto) {
	    Ingredients ingredient = ingredientsRepository.findById(dto.getIngredientsId())
	        .orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다: " + dto.getIngredientsId()));

	    if (dto.getIngredientsName() != null) {
	        ingredient.setIngredientsName(dto.getIngredientsName());
	    }
	    if (dto.getQuantity() != null) {
	        ingredient.setQuantity(dto.getQuantity());
	    }
	    if (dto.getExpirationDate() != null) {
	        ingredient.setExpirationDate(dto.getExpirationDateAsLocalDate());
	    }
	    if (dto.getStorageLocation() != null) {
	        ingredient.setStorageLocation(dto.getStorageLocation());
	    }
	    if (dto.getClassId() != null) {
	        ClassEntity classEntity = classRepository.findById(dto.getClassId())
	            .orElseThrow(() -> new IllegalArgumentException("클래스를 찾을 수 없습니다: " + dto.getClassId()));
	        ingredient.setClassEntity(classEntity);
	    }
	    if (dto.getRefrigeratorId() != 0) { // 0이 아닌 값만 변경
	        Refrigerator refrigerator = refrigeratorRepository.findById(dto.getRefrigeratorId())
	            .orElseThrow(() -> new IllegalArgumentException("냉장고를 찾을 수 없습니다: " + dto.getRefrigeratorId()));
	        ingredient.setRefrigerator(refrigerator);
	    }
	}
	
	@Transactional
	public void updateNote(int ingredientsId, String note) {
	    Ingredients ingredient = ingredientsRepository.findById(ingredientsId)
	        .orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다: " + ingredientsId));
	    ingredient.setNote(note);
	}
	
	@Transactional
	public void deleteIngredient(int ingredientsId) {
	    Ingredients ingredient = ingredientsRepository.findById(ingredientsId)
	        .orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다: " + ingredientsId));
	    ingredientsRepository.delete(ingredient);
	}
	
	



}
