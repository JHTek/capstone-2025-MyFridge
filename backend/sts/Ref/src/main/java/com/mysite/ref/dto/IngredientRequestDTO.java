package com.mysite.ref.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRequestDTO {
	
	private int refrigeratorId;
	private String ingredientsName;
	private int quantity;
	private String expirationDate;
	private int storageLocation;
	private int classId;

	public LocalDate getExpirationDateAsLocalDate() {
        return LocalDate.parse(expirationDate); // "yyyy-MM-dd" 형식으로 파싱
    }
}

