package com.example.sonya.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotNull(message = "Calories are required")
    @Positive(message = "Calories must be positive")
    private Double calories;
    
    @NotNull(message = "Protein is required")
    @Positive(message = "Protein must be positive")
    private Double protein;
    
    @NotNull(message = "Fats are required")
    @Positive(message = "Fats must be positive")
    private Double fats;
    
    @NotNull(message = "Carbohydrates are required")
    @Positive(message = "Carbohydrates must be positive")
    private Double carbohydrates;
    
    private String servingSize;
}

