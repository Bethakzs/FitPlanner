package com.example.sonya.service.impl;

import com.example.sonya.dto.exercise.ExerciseDto;
import com.example.sonya.dto.exercise.ExerciseRequest;
import com.example.sonya.entity.Exercise;
import com.example.sonya.mapper.ExerciseMapper;
import com.example.sonya.repository.ExerciseRepository;
import com.example.sonya.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    @Override
    @Transactional(readOnly = true)
    public ExerciseDto getExerciseInfo(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return exerciseMapper.toDto(exercise);
    }

    @Override
    @Transactional
    public ExerciseDto createExercise(ExerciseRequest request) {
        Exercise exercise = exerciseMapper.toEntity(request);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDto(savedExercise);
    }

    @Override
    @Transactional
    public ExerciseDto updateExercise(Long exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        
        exerciseMapper.updateFromDto(exercise, request);
        Exercise updatedExercise = exerciseRepository.save(exercise);
        
        return exerciseMapper.toDto(updatedExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId) {
        exerciseRepository.deleteById(exerciseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDto> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByType(String type) {
        return exerciseRepository.findByType(type).stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDto> searchByName(String name) {
        return exerciseRepository.findByNameContainingIgnoreCase(name).stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exercise> findById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId);
    }
}
