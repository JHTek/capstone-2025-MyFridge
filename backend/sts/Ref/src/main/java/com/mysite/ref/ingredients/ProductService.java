package com.mysite.ref.ingredients;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysite.ref.dto.ProductDto;

@Service
public class ProductService {
	
	@Autowired
    private IngredientsRepository ingredientsRepository;

	 public List<ProductDto> getExpiringProducts(String userId, int alertDays) {
	        
	        
	        LocalDate today = LocalDate.now();
	        LocalDate expirationLimitDate = today.plusDays(alertDays);

	        
	        List<Ingredients> expiringIngredients = ingredientsRepository.findExpiringIngredientsForUser(
	                userId, expirationLimitDate);

	        
	        return expiringIngredients.stream()
	                .map(this::convertToDto) 
	                .collect(Collectors.toList()); 
	    }
	 private ProductDto convertToDto(Ingredients ingredient) {
	        return new ProductDto(
	                ingredient.getIngredientsId(),
	                ingredient.getRefrigerator().getRefrigeratorId(),
	                ingredient.getRefrigerator().getRefrigeratorName(),// 냉장고 이름 추가
	                ingredient.getIngredientsName(),
	                ingredient.getQuantity(),
	                ingredient.getExpirationDate().toString(), 
	                ingredient.getStorageLocation(),
	                ingredient.getClassEntity().getCategory(), 
	                ingredient.getNote()
	        );
	    }
}
