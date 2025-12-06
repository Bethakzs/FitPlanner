package com.example.sonya.mapper;

import com.example.sonya.dto.recommendation.RecommendationDto;
import com.example.sonya.entity.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationMapper {

    private final ExerciseMapper exerciseMapper;

    public RecommendationDto toDto(Recommendation recommendation) {
        if (recommendation == null) {
            return null;
        }
        
        return RecommendationDto.builder()
                .id(recommendation.getId())
                .type(recommendation.getType())
                .description(recommendation.getDescription())
                .exercises(recommendation.getExercises().stream()
                        .map(exerciseMapper::toDto)
                        .collect(Collectors.toList()))
                .createdAt(recommendation.getCreatedAt())
                .build();
    }
}

