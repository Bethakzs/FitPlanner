package com.example.sonya.service.impl;

import com.example.sonya.dto.physical.PhysicalCharacteristicsDto;
import com.example.sonya.dto.recommendation.RecommendationDto;
import com.example.sonya.entity.Exercise;
import com.example.sonya.entity.Recommendation;
import com.example.sonya.entity.User;
import com.example.sonya.enums.ActivityLevel;
import com.example.sonya.enums.BMICategory;
import com.example.sonya.mapper.RecommendationMapper;
import com.example.sonya.repository.ExerciseRepository;
import com.example.sonya.repository.RecommendationRepository;
import com.example.sonya.service.ExerciseService;
import com.example.sonya.service.PhysicalCharacteristicsService;
import com.example.sonya.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ExerciseRepository exerciseRepository;
    private final RecommendationMapper recommendationMapper;
    private final PhysicalCharacteristicsService physicalCharacteristicsService;
    private final ExerciseService exerciseService;

    @Override
    @Transactional
    public RecommendationDto generateRecommendations(User user, String type, String description, List<Long> exerciseIds) {
        List<Exercise> exercises = new ArrayList<>();
        if (exerciseIds != null && !exerciseIds.isEmpty()) {
            exercises = exerciseIds.stream()
                    .map(id -> exerciseRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Exercise not found: " + id)))
                    .collect(Collectors.toList());
        }
        
        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .type(type)
                .description(description)
                .exercises(exercises)
                .createdAt(LocalDateTime.now())
                .build();
        
        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        return recommendationMapper.toDto(savedRecommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationDto getRecommendationById(Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));
        return recommendationMapper.toDto(recommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getUserRecommendations(User user) {
        return recommendationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getUserRecommendationsByType(User user, String type) {
        return recommendationRepository.findByUserAndType(user, type).stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(Long recommendationId, String description, List<Long> exerciseIds) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));
        
        if (description != null) {
            recommendation.setDescription(description);
        }
        
        if (exerciseIds != null && !exerciseIds.isEmpty()) {
            List<Exercise> exercises = exerciseIds.stream()
                    .map(id -> exerciseRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Exercise not found: " + id)))
                    .collect(Collectors.toList());
            recommendation.setExercises(exercises);
        }
        
        Recommendation updatedRecommendation = recommendationRepository.save(recommendation);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    @Override
    @Transactional
    public void deleteRecommendation(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Override
    @Transactional
    public RecommendationDto generateRecommendations(User user) {
        PhysicalCharacteristicsDto characteristics;
        try {
            characteristics = physicalCharacteristicsService.getByUser(user);
        } catch (Exception e) {
            characteristics = physicalCharacteristicsService.calculateAndSave(user);
        }
        
        // Generate recommendations based on BMI category
        String type = "Mixed Training";
        StringBuilder description = new StringBuilder();
        List<Long> recommendedExerciseIds = new ArrayList<>();
        
        // Select exercises based on BMI category and activity level
        if (characteristics.getBmiCategory() == BMICategory.OVERWEIGHT || 
            characteristics.getBmiCategory() == BMICategory.OBESE) {
            
            type = "Weight Loss Program";
            description.append("Recommendations for weight loss:\n");
            description.append("- Create a caloric deficit of 300-500 kcal per day\n");
            description.append("- Increase physical activity\n");
            description.append("- Focus on cardio exercises\n");
            description.append("- Combine aerobic and strength training\n");
            
            recommendedExerciseIds = exerciseService.getExercisesByType("cardio").stream()
                    .limit(3)
                    .map(ex -> ex.getId())
                    .toList();
            
        } else if (characteristics.getBmiCategory() == BMICategory.UNDERWEIGHT) {
            
            type = "Weight Gain Program";
            description.append("Recommendations for weight gain:\n");
            description.append("- Increase caloric intake\n");
            description.append("- Add strength training\n");
            description.append("- Eat 5-6 meals per day\n");
            description.append("- Focus on protein-rich foods\n");
            
            recommendedExerciseIds = exerciseService.getExercisesByType("strength").stream()
                    .limit(3)
                    .map(ex -> ex.getId())
                    .toList();
            
        } else {
            type = "Maintenance Program";
            description.append("Recommendations for maintaining weight:\n");
            description.append("- Continue balanced nutrition\n");
            description.append("- Maintain regular physical activity\n");
            description.append("- Combine cardio and strength training\n");
            description.append("- Stay consistent with your routine\n");
            
            recommendedExerciseIds = exerciseService.getAllExercises().stream()
                    .limit(4)
                    .map(ex -> ex.getId())
                    .toList();
        }
        
        // Additional tips based on activity level
        if (user.getActivityLevel() == ActivityLevel.LOW) {
            description.append("\nYour activity level is low. We recommend:\n");
            description.append("- Start with 30 minutes of walking daily\n");
            description.append("- Gradually increase intensity\n");
            description.append("- Set achievable weekly goals\n");
        }
        
        return generateRecommendations(user, type, description.toString(), recommendedExerciseIds);
    }
}
