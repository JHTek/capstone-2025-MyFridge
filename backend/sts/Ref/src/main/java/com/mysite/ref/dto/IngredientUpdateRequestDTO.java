package com.mysite.ref.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientUpdateRequestDTO {
	private int refrigeratorId;
    private int ingredientsId;        
    private String ingredientsName;
    private Integer quantity;
    private String expirationDate;
    private Integer storageLocation;
    private Integer classId;        
    
	public LocalDate getExpirationDateAsLocalDate() {
        return LocalDate.parse(expirationDate); // "yyyy-MM-dd" 형식으로 파싱
    }
}