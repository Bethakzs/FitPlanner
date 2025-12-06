package com.example.sonya.dto.report;

import com.example.sonya.enums.ActivityLevel;
import com.example.sonya.enums.AllergyType;
import com.example.sonya.enums.BMICategory;
import com.example.sonya.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionReportResponse {

    private Long reportId;
    private LocalDateTime createdAt;

    private Double weight;
    private Double height;
    private Gender gender;
    private ActivityLevel activityLevel;
    private Double waterIntake;
    private Set<AllergyType> allergies;

    private Double bmi;
    private BMICategory bmiCategory;
    private String bmiInterpretation;

    private Double bmr;
    private Double tdee;
    private Double targetWeight;
    private String weightRecommendation;

    private MacronutrientsDto macronutrients;
    private WaterRecommendationDto waterRecommendation;
    private FoodRecommendationsDto foodRecommendations;
    private List<String> activityRecommendations;
    private List<String> dietaryRestrictions;
    private List<String> generalTips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MacronutrientsDto {
        private Double dailyCalories;
        private Double protein;
        private Double fats;
        private Double carbs;
        private String proteinPercentage;
        private String fatsPercentage;
        private String carbsPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaterRecommendationDto {
        private Double currentIntake;
        private Double recommendedIntake;
        private String status;
        private String advice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodRecommendationsDto {
        private List<String> proteinFoods;
        private List<String> fatFoods;
        private List<String> carbFoods;
    }
}

