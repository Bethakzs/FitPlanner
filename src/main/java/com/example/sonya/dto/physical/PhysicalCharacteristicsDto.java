package com.example.sonya.dto.physical;

import com.example.sonya.enums.BMICategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalCharacteristicsDto {
    private Long id;
    private Double bmi;
    private Double bmr;
    private BMICategory bmiCategory;
    private Double optimalWeight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

