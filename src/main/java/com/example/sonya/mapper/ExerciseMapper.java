package com.example.sonya.mapper;

import com.example.sonya.dto.exercise.ExerciseDto;
import com.example.sonya.dto.exercise.ExerciseRequest;
import com.example.sonya.entity.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public ExerciseDto toDto(Exercise exercise) {
        if (exercise == null) {
            return null;
        }
        
        return ExerciseDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .type(exercise.getType())
                .caloriesBurned(exercise.getCaloriesBurned())
                .durationMinutes(exercise.getDurationMinutes())
                .description(exercise.getDescription())
                .build();
    }

    public Exercise toEntity(ExerciseRequest request) {
        if (request == null) {
            return null;
        }
        
        return Exercise.builder()
                .name(request.getName())
                .type(request.getType())
                .caloriesBurned(request.getCaloriesBurned())
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .build();
    }

    public Exercise updateFromDto(Exercise exercise, ExerciseRequest request) {
        if (request.getName() != null) {
            exercise.setName(request.getName());
        }
        if (request.getType() != null) {
            exercise.setType(request.getType());
        }
        if (request.getCaloriesBurned() != null) {
            exercise.setCaloriesBurned(request.getCaloriesBurned());
        }
        if (request.getDurationMinutes() != null) {
            exercise.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getDescription() != null) {
            exercise.setDescription(request.getDescription());
        }
        
        return exercise;
    }
}

