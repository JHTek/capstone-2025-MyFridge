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
	private LocalDate expirationDate;
	private int storageLocation;
	private int classId;
}
