package com.mysite.ref.ingredients;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.ref.dto.IngredientRequestDTO;
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
        Class defaultClass = classRepository.findById(1)//test용
        		.orElseThrow(() -> new IllegalArgumentException("Default class not found"));
        
		for (IngredientRequestDTO dto : ingredientsDtoList) {
            Refrigerator refrigerator = refrigeratorRepository.findById(dto.getRefrigeratorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid refrigerator ID"));

            //Class type = classRepository.findById(dto.getClassId())
              //      .orElseThrow(() -> new IllegalArgumentException("Invalid class ID"));

            Ingredients ingredient = new Ingredients();
            ingredient.setIngredientsName(dto.getIngredientsName());
            ingredient.setQuantity(dto.getQuantity());
            ingredient.setExpirationDate(dto.getExpirationDate());
            ingredient.setStorageLocation(dto.getStorageLocation());
            ingredient.setRefrigerator(refrigerator);
            //ingredient.setType(type);
            
            ingredient.setType(defaultClass); // test용
            ingredientsRepository.save(ingredient);
        }
    }

}
