package com.mysite.ref.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private int ingredientsId;
    private int refrigeratorId;
    private String refrigeratorName;
    private String ingredientsName;
    private int quantity;
    private String expirationDate;
    private int storageLocation;
    private String category;
    private String note;
}
