package com.example.sonya.dto.diet;

import com.example.sonya.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDto {
    private Long id;
    private Double totalCalories;
    private String balanceDescription;
    private Double totalProtein;
    private Double totalFats;
    private Double totalCarbs;
    private List<ProductDto> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

