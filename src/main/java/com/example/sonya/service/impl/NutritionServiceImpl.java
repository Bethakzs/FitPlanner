package com.example.sonya.service.impl;

import com.example.sonya.dto.report.NutritionReportResponse;
import com.example.sonya.dto.report.UserMetricsRequest;
import com.example.sonya.entity.NutritionReport;
import com.example.sonya.entity.PhysicalCharacteristics;
import com.example.sonya.entity.Product;
import com.example.sonya.entity.User;
import com.example.sonya.enums.*;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.NutritionReportRepository;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.*;
import com.example.sonya.dto.user.UpdateUserDataRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final UserRepository userRepository;
    private final NutritionReportRepository nutritionReportRepository;
    private final PhysicalCharacteristicsService physicalCharacteristicsService;
    private final DietService dietService;
    private final ProductService productService;
    private final RecommendationService recommendationService;
    private final UserService userService;

    private static final double PROTEIN_CALORIES_PER_GRAM = 4.0;
    private static final double CARBS_CALORIES_PER_GRAM = 4.0;
    private static final double FATS_CALORIES_PER_GRAM = 9.0;

    @Override
    @Transactional
    public NutritionReportResponse generateReport(UserMetricsRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));

        boolean userDataChanged = false;
        if (request.getWeight() != null && !request.getWeight().equals(user.getWeight())) {
            user.setWeight(request.getWeight());
            userDataChanged = true;
        }
        if (request.getHeight() != null && !request.getHeight().equals(user.getHeight())) {
            user.setHeight(request.getHeight());
            userDataChanged = true;
        }
        if (request.getGender() != null && !request.getGender().equals(user.getGender())) {
            user.setGender(request.getGender());
            userDataChanged = true;
        }
        if (request.getActivityLevel() != null && !request.getActivityLevel().equals(user.getActivityLevel())) {
            user.setActivityLevel(request.getActivityLevel());
            userDataChanged = true;
        }
        
        if (userDataChanged) {
            user.setModifiedDate(System.currentTimeMillis());
            userRepository.save(user);
        }

        double bmi = calculateBMI(request.getWeight(), request.getHeight());
        BMICategory bmiCategory = determineBMICategory(bmi);
        double bmr = calculateBMR(request.getWeight(), request.getHeight(), request.getGender());
        double tdee = calculateTDEE(bmr, request.getActivityLevel().getMultiplier());
        double targetWeight = calculateTargetWeight(request.getHeight(), bmiCategory);
        double recommendedWater = calculateRecommendedWater(request.getWeight(), request.getActivityLevel());

        double dailyProtein = (tdee * 0.30) / PROTEIN_CALORIES_PER_GRAM;
        double dailyFats = (tdee * 0.25) / FATS_CALORIES_PER_GRAM;
        double dailyCarbs = (tdee * 0.45) / CARBS_CALORIES_PER_GRAM;

        NutritionReport report = null;
        if (request.getSaveReport()) {
            if (user.getWeight() != null && user.getHeight() != null && 
                user.getGender() != null && user.getAge() != null) {
                try {
                    physicalCharacteristicsService.calculateAndSave(user);
                } catch (Exception e) {
                    System.err.println("Failed to save physical characteristics: " + e.getMessage());
                }
            }
            
            report = NutritionReport.builder()
                    .user(user)
                    .weight(request.getWeight())
                    .height(request.getHeight())
                    .gender(request.getGender())
                    .activityLevel(request.getActivityLevel())
                    .waterIntake(request.getWaterIntake())
                    .allergies(request.getAllergies() != null ? request.getAllergies() : new HashSet<>())
                    .bmi(bmi)
                    .bmiCategory(bmiCategory)
                    .bmr(bmr)
                    .tdee(tdee)
                    .targetWeight(targetWeight)
                    .dailyProtein(dailyProtein)
                    .dailyFats(dailyFats)
                    .dailyCarbs(dailyCarbs)
                    .recommendedWater(recommendedWater)
                    .createdAt(LocalDateTime.now())
                    .build();

            report = nutritionReportRepository.save(report);
            
            try {
                createAutoDiet(user, request.getAllergies(), tdee);
            } catch (Exception e) {
                System.err.println("Failed to create auto diet: " + e.getMessage());
            }
        }

        return buildResponse(request, report != null ? report.getId() : null, bmi, bmiCategory,
                bmr, tdee, targetWeight, dailyProtein, dailyFats, dailyCarbs, recommendedWater);
    }
    
    private void createAutoDiet(User user, Set<AllergyType> allergies, double tdee) {
        List<FoodItem> proteinFoods = FoodItem.getByCategoryFiltered(FoodCategory.PROTEIN, allergies);
        List<FoodItem> fatFoods = FoodItem.getByCategoryFiltered(FoodCategory.FATS, allergies);
        List<FoodItem> carbFoods = FoodItem.getByCategoryFiltered(FoodCategory.CARBS, allergies);
        
        List<Long> selectedProductIds = new ArrayList<>();
        
        addRandomProducts(selectedProductIds, proteinFoods, 6);
        addRandomProducts(selectedProductIds, fatFoods, 6);
        addRandomProducts(selectedProductIds, carbFoods, 8);
        
        if (!selectedProductIds.isEmpty()) {
            dietService.createDiet(user, selectedProductIds);
        }
    }
    
    private void addRandomProducts(List<Long> productIds, List<FoodItem> foodItems, int count) {
        if (foodItems.isEmpty()) return;
        
        List<FoodItem> shuffled = new ArrayList<>(foodItems);
        Collections.shuffle(shuffled);
        
        for (int i = 0; i < Math.min(count, shuffled.size()); i++) {
            FoodItem foodItem = shuffled.get(i);
            try {
                List<Product> products = productService.findByName(foodItem.getDisplayName());
                if (!products.isEmpty()) {
                    Long productId = products.get(0).getId();
                    if (!productIds.contains(productId)) {
                        productIds.add(productId);
                    }
                }
            } catch (Exception e) {
                System.err.println("Product not found: " + foodItem.getDisplayName());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NutritionReportResponse> getUserReports(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));

        List<NutritionReport> reports = nutritionReportRepository.findTop10ByUserOrderByCreatedAtDesc(user);

        return reports.stream()
                .map(report -> buildResponse(
                        toRequest(report),
                        report.getId(),
                        report.getBmi(),
                        report.getBmiCategory(),
                        report.getBmr(),
                        report.getTdee(),
                        report.getTargetWeight(),
                        report.getDailyProtein(),
                        report.getDailyFats(),
                        report.getDailyCarbs(),
                        report.getRecommendedWater()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        nutritionReportRepository.deleteById(reportId);
    }

    @Override
    @Transactional
    public NutritionReportResponse generateCompleteReport(UserMetricsRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
        
        if (request.getAge() != null) {
            UpdateUserDataRequest updateRequest = new UpdateUserDataRequest();
            updateRequest.setAge(request.getAge());
            updateRequest.setWeight(request.getWeight());
            updateRequest.setHeight(request.getHeight());
            updateRequest.setGender(request.getGender());
            updateRequest.setActivityLevel(request.getActivityLevel());
            
            userService.updateUserData(user.getId(), updateRequest);
            
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
        }
        
        if (user.getAge() != null && user.getWeight() != null && user.getHeight() != null) {
            try {
                physicalCharacteristicsService.calculateAndSave(user);
            } catch (Exception e) {
                System.err.println("Failed to calculate physical characteristics: " + e.getMessage());
            }
        }
        
        NutritionReportResponse response = generateReport(request, userEmail);
        
        try {
            recommendationService.generateRecommendations(user);
        } catch (Exception e) {
            System.err.println("Failed to generate recommendations: " + e.getMessage());
        }
        
        return response;
    }

    private double calculateBMI(double weight, double height) {
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    private BMICategory determineBMICategory(double bmi) {
        if (bmi < 18.5) return BMICategory.UNDERWEIGHT;
        if (bmi < 25.0) return BMICategory.NORMAL;
        if (bmi < 30.0) return BMICategory.OVERWEIGHT;
        return BMICategory.OBESE;
    }

    private double calculateBMR(double weight, double height, Gender gender) {
        if (gender == Gender.MALE) {
            return 10 * weight + 6.25 * height - 5 * 30 + 5;
        } else {
            return 10 * weight + 6.25 * height - 5 * 30 - 161;
        }
    }

    private double calculateTDEE(double bmr, double activityMultiplier) {
        return bmr * activityMultiplier;
    }

    private double calculateTargetWeight(double height, BMICategory category) {
        double heightInMeters = height / 100.0;
        double targetBMI = 22.0;

        if (category == BMICategory.UNDERWEIGHT) {
            targetBMI = 20.0;
        } else if (category == BMICategory.OVERWEIGHT || category == BMICategory.OBESE) {
            targetBMI = 24.0;
        }

        return targetBMI * (heightInMeters * heightInMeters);
    }

    private double calculateRecommendedWater(double weight, com.example.sonya.enums.ActivityLevel activityLevel) {
        double baseWater = weight * 0.033;

        if (activityLevel == com.example.sonya.enums.ActivityLevel.MEDIUM) {
            baseWater += 0.5;
        } else if (activityLevel == com.example.sonya.enums.ActivityLevel.HIGH) {
            baseWater += 1.0;
        }

        return baseWater;
    }

    private NutritionReportResponse buildResponse(UserMetricsRequest request, Long reportId,
                                                   double bmi, BMICategory bmiCategory,
                                                   double bmr, double tdee, double targetWeight,
                                                   double dailyProtein, double dailyFats, double dailyCarbs,
                                                   double recommendedWater) {

        return NutritionReportResponse.builder()
                .reportId(reportId)
                .createdAt(LocalDateTime.now())
                .weight(request.getWeight())
                .height(request.getHeight())
                .gender(request.getGender())
                .activityLevel(request.getActivityLevel())
                .waterIntake(request.getWaterIntake())
                .allergies(request.getAllergies())
                .bmi(Math.round(bmi * 100.0) / 100.0)
                .bmiCategory(bmiCategory)
                .bmiInterpretation(getBMIInterpretation(bmiCategory))
                .bmr((double) Math.round(bmr))
                .tdee((double) Math.round(tdee))
                .targetWeight(Math.round(targetWeight * 100.0) / 100.0)
                .weightRecommendation(getWeightRecommendation(request.getWeight(), targetWeight, bmiCategory))
                .macronutrients(buildMacronutrientsDto(tdee, dailyProtein, dailyFats, dailyCarbs))
                .waterRecommendation(buildWaterRecommendationDto(request.getWaterIntake(), recommendedWater))
                .foodRecommendations(buildFoodRecommendations(request.getAllergies()))
                .activityRecommendations(getActivityRecommendations(request.getActivityLevel(), bmiCategory))
                .dietaryRestrictions(getDietaryRestrictions(request.getAllergies()))
                .generalTips(getGeneralTips(bmiCategory, request.getActivityLevel()))
                .build();
    }

    private NutritionReportResponse.MacronutrientsDto buildMacronutrientsDto(double tdee,
                                                                              double protein, double fats, double carbs) {
        return NutritionReportResponse.MacronutrientsDto.builder()
                .dailyCalories((double) Math.round(tdee))
                .protein((double) Math.round(protein))
                .fats((double) Math.round(fats))
                .carbs((double) Math.round(carbs))
                .proteinPercentage("30%")
                .fatsPercentage("25%")
                .carbsPercentage("45%")
                .build();
    }

    private NutritionReportResponse.WaterRecommendationDto buildWaterRecommendationDto(double currentIntake,
                                                                                        double recommendedIntake) {
        String status;
        String advice;

        double difference = currentIntake - recommendedIntake;

        if (Math.abs(difference) <= 0.2) {
            status = "OPTIMAL";
            advice = "Your water intake is perfect! Keep it up.";
        } else if (difference < 0) {
            status = "LOW";
            advice = String.format("Try to drink %.1f more liters of water daily.", Math.abs(difference));
        } else {
            status = "HIGH";
            advice = "Your water intake is above recommendations, which is generally fine unless you have medical restrictions.";
        }

        return NutritionReportResponse.WaterRecommendationDto.builder()
                .currentIntake(Math.round(currentIntake * 10.0) / 10.0)
                .recommendedIntake(Math.round(recommendedIntake * 10.0) / 10.0)
                .status(status)
                .advice(advice)
                .build();
    }

    private String getBMIInterpretation(BMICategory category) {
        return switch (category) {
            case UNDERWEIGHT -> "Your BMI indicates underweight. Consider consulting a nutritionist.";
            case NORMAL -> "Your BMI is in the healthy range. Maintain your current lifestyle!";
            case OVERWEIGHT -> "Your BMI indicates overweight. Consider a balanced diet and regular exercise.";
            case OBESE -> "Your BMI indicates obesity. We recommend consulting a healthcare professional.";
        };
    }

    private String getWeightRecommendation(double currentWeight, double targetWeight, BMICategory category) {
        if (category == BMICategory.NORMAL) {
            return "Your weight is optimal. Focus on maintaining it through a balanced diet and regular activity.";
        }

        double difference = currentWeight - targetWeight;
        String direction = difference > 0 ? "lose" : "gain";
        double absDiff = Math.abs(difference);

        return String.format("To reach your target weight, you should %s approximately %.1f kg. " +
                "Aim for a healthy rate of 0.5-1 kg per week.", direction, absDiff);
    }

    private List<String> getActivityRecommendations(com.example.sonya.enums.ActivityLevel activityLevel,
                                                     BMICategory bmiCategory) {
        List<String> recommendations = new ArrayList<>();

        if (activityLevel == com.example.sonya.enums.ActivityLevel.LOW) {
            recommendations.add("Aim for at least 150 minutes of moderate aerobic activity per week");
            recommendations.add("Start with walking 30 minutes daily and gradually increase intensity");
            recommendations.add("Include strength training exercises 2-3 times per week");
        } else if (activityLevel == com.example.sonya.enums.ActivityLevel.MEDIUM) {
            recommendations.add("Maintain your current activity level of 3-5 workouts per week");
            recommendations.add("Mix cardio and strength training for optimal results");
            recommendations.add("Consider adding HIIT sessions for increased calorie burn");
        } else {
            recommendations.add("Great job maintaining high activity levels!");
            recommendations.add("Ensure adequate rest and recovery between intense workouts");
            recommendations.add("Focus on proper nutrition to support your training");
        }

        if (bmiCategory == BMICategory.OVERWEIGHT || bmiCategory == BMICategory.OBESE) {
            recommendations.add("Low-impact exercises like swimming or cycling are recommended");
        }

        return recommendations;
    }

    private List<String> getDietaryRestrictions(Set<AllergyType> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return List.of("No dietary restrictions detected. You have flexibility in meal planning.");
        }

        List<String> restrictions = new ArrayList<>();
        restrictions.add("Avoid foods containing the following allergens:");

        for (AllergyType allergy : allergies) {
            switch (allergy) {
                case GLUTEN -> restrictions.add("- Gluten: Avoid wheat, barley, rye, and their derivatives");
                case LACTOSE -> restrictions.add("- Lactose: Avoid dairy products or choose lactose-free alternatives");
                case NUTS -> restrictions.add("- Nuts: Avoid all tree nuts and products containing them");
                case EGGS -> restrictions.add("- Eggs: Avoid eggs and egg-containing products");
                case SEAFOOD -> restrictions.add("- Seafood: Avoid fish, shellfish, and seafood products");
                case SOY -> restrictions.add("- Soy: Avoid soybeans and soy-based products");
            }
        }

        return restrictions;
    }

    private NutritionReportResponse.FoodRecommendationsDto buildFoodRecommendations(Set<AllergyType> allergies) {
        List<FoodItem> proteinFoods = FoodItem.getByCategoryFiltered(FoodCategory.PROTEIN, allergies);
        List<FoodItem> fatFoods = FoodItem.getByCategoryFiltered(FoodCategory.FATS, allergies);
        List<FoodItem> carbFoods = FoodItem.getByCategoryFiltered(FoodCategory.CARBS, allergies);

        return NutritionReportResponse.FoodRecommendationsDto.builder()
                .proteinFoods(getRandomFoods(proteinFoods, 5))
                .fatFoods(getRandomFoods(fatFoods, 5))
                .carbFoods(getRandomFoods(carbFoods, 5))
                .build();
    }

    private List<String> getRandomFoods(List<FoodItem> foods, int count) {
        if (foods.isEmpty()) {
            return List.of("No suitable foods found");
        }

        List<FoodItem> shuffled = new ArrayList<>(foods);
        Collections.shuffle(shuffled);

        return shuffled.stream()
                .limit(Math.min(count, shuffled.size()))
                .map(FoodItem::getDisplayName)
                .collect(Collectors.toList());
    }

    private List<String> getGeneralTips(BMICategory bmiCategory, com.example.sonya.enums.ActivityLevel activityLevel) {
        List<String> tips = new ArrayList<>();

        tips.add("Eat 4-5 small meals throughout the day to maintain steady energy levels");
        tips.add("Include protein in every meal to support muscle maintenance and satiety");
        tips.add("Choose whole grains over refined carbohydrates");
        tips.add("Fill half your plate with vegetables at main meals");
        tips.add("Limit processed foods and added sugars");
        tips.add("Get 7-9 hours of quality sleep per night");

        if (bmiCategory == BMICategory.OVERWEIGHT || bmiCategory == BMICategory.OBESE) {
            tips.add("Create a moderate caloric deficit of 300-500 calories daily for steady weight loss");
            tips.add("Track your food intake to increase awareness of portion sizes");
        } else if (bmiCategory == BMICategory.UNDERWEIGHT) {
            tips.add("Increase caloric intake with nutrient-dense foods");
            tips.add("Add healthy fats like nuts, avocados, and olive oil to meals");
        }

        return tips;
    }

    private UserMetricsRequest toRequest(NutritionReport report) {
        UserMetricsRequest request = new UserMetricsRequest();
        request.setWeight(report.getWeight());
        request.setHeight(report.getHeight());
        request.setGender(report.getGender());
        request.setActivityLevel(report.getActivityLevel());
        request.setWaterIntake(report.getWaterIntake());
        request.setAllergies(report.getAllergies());
        return request;
    }
}

