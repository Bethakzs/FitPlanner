package com.example.sonya.mapper;

import com.example.sonya.dto.physical.PhysicalCharacteristicsDto;
import com.example.sonya.entity.PhysicalCharacteristics;
import org.springframework.stereotype.Component;

@Component
public class PhysicalCharacteristicsMapper {

    public PhysicalCharacteristicsDto toDto(PhysicalCharacteristics characteristics) {
        if (characteristics == null) {
            return null;
        }
        
        return PhysicalCharacteristicsDto.builder()
                .id(characteristics.getId())
                .bmi(characteristics.getBmi())
                .bmr(characteristics.getBmr())
                .bmiCategory(characteristics.getBmiCategory())
                .optimalWeight(characteristics.getOptimalWeight())
                .createdAt(characteristics.getCreatedAt())
                .updatedAt(characteristics.getUpdatedAt())
                .build();
    }
}

