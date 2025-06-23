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

    public LocalDate getExpirationDateAsLocalDate() {
        if (expirationDate == null || expirationDate.isEmpty()) {
            return null; // null 반환 허용
        }
        return LocalDate.parse(expirationDate);
    }
}

