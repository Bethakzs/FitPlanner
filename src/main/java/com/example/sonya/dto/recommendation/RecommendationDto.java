package com.example.sonya.dto.recommendation;

import com.example.sonya.dto.exercise.ExerciseDto;
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
public class RecommendationDto {
    private Long id;
    private String type;
    private String description;
    private List<ExerciseDto> exercises;
    private LocalDateTime createdAt;
}

