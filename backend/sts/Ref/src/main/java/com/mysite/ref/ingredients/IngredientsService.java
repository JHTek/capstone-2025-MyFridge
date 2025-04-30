package com.mysite.ref.ingredients;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.ref.dto.IngredientRequestDTO;
import com.mysite.ref.dto.IngredientResponseDTO;
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
        ClassEntity defaultClass = classRepository.findById(2)//test용
        		.orElseThrow(() -> new IllegalArgumentException("Default class not found"));
        
		for (IngredientRequestDTO dto : ingredientsDtoList) {
            Refrigerator refrigerator = refrigeratorRepository.findById(dto.getRefrigeratorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid refrigerator ID"));

            //Class type = classRepository.findById(dto.getClassId())
              //      .orElseThrow(() -> new IllegalArgumentException("Invalid class ID"));

            Ingredients ingredient = new Ingredients();
            ingredient.setIngredientsName(dto.getIngredientsName());
            ingredient.setQuantity(dto.getQuantity());
            ingredient.setExpirationDate(dto.getExpirationDateAsLocalDate());
            ingredient.setStorageLocation(dto.getStorageLocation());
            ingredient.setRefrigerator(refrigerator);
            //ingredient.setType(type);
            
            ingredient.setClassEntity(defaultClass); // test용
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
					return dto;
				})
				.collect(Collectors.toList());
	}

}
