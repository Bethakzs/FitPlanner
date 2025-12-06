package com.example.sonya.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double fats;
    private Double carbohydrates;
    private String servingSize;
}

