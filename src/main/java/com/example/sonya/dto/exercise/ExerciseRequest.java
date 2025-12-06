package com.example.sonya.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequest {
    
    @NotBlank(message = "Exercise name is required")
    private String name;
    
    @NotBlank(message = "Exercise type is required")
    private String type;
    
    @Positive(message = "Calories burned must be positive")
    private Double caloriesBurned;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;
    
    private String description;
}

