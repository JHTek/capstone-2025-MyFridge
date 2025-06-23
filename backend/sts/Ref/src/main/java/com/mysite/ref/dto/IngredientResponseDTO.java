package com.mysite.ref.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientResponseDTO {
    private int ingredientsId;
    private String ingredientsName;
    private int quantity;
    private LocalDate expirationDate;
    private int storageLocation;
    private String refrigeratorName; // Refrigerator 엔티티의 이름을 저장할 필드
    private String category;
    private String note;
    
}
