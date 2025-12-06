package com.example.sonya.service;

import com.example.sonya.dto.exercise.ExerciseDto;
import com.example.sonya.dto.exercise.ExerciseRequest;
import com.example.sonya.entity.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseService {
    
    ExerciseDto getExerciseInfo(Long exerciseId);
    
    ExerciseDto createExercise(ExerciseRequest request);
    
    ExerciseDto updateExercise(Long exerciseId, ExerciseRequest request);
    
    void deleteExercise(Long exerciseId);
    
    List<ExerciseDto> getAllExercises();
    
    List<ExerciseDto> getExercisesByType(String type);
    
    List<ExerciseDto> searchByName(String name);
    
    Optional<Exercise> findById(Long exerciseId);
}
