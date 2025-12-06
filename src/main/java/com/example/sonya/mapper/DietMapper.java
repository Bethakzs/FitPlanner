package com.example.sonya.mapper;

import com.example.sonya.dto.diet.DietDto;
import com.example.sonya.entity.Diet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DietMapper {

    private final ProductMapper productMapper;

    public DietDto toDto(Diet diet) {
        if (diet == null) {
            return null;
        }
        
        return DietDto.builder()
                .id(diet.getId())
                .totalCalories(diet.getTotalCalories())
                .balanceDescription(diet.getBalanceDescription())
                .totalProtein(diet.getTotalProtein())
                .totalFats(diet.getTotalFats())
                .totalCarbs(diet.getTotalCarbs())
                .products(diet.getProducts().stream()
                        .map(productMapper::toDto)
                        .collect(Collectors.toList()))
                .createdAt(diet.getCreatedAt())
                .updatedAt(diet.getUpdatedAt())
                .build();
    }
}

