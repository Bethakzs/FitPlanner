package com.example.sonya.entity;

import com.example.sonya.enums.ActivityLevel;
import com.example.sonya.enums.AllergyType;
import com.example.sonya.enums.BMICategory;
import com.example.sonya.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nutrition_reports")
public class NutritionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double height;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevel activityLevel;

    @Column(name = "water_intake", nullable = false)
    private Double waterIntake;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "report_allergies", joinColumns = @JoinColumn(name = "report_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergy_type")
    private Set<AllergyType> allergies;

    @Column(nullable = false)
    private Double bmi;

    @Enumerated(EnumType.STRING)
    @Column(name = "bmi_category", nullable = false)
    private BMICategory bmiCategory;

    @Column(nullable = false)
    private Double bmr;

    @Column(nullable = false)
    private Double tdee;

    @Column(name = "target_weight")
    private Double targetWeight;

    @Column(name = "daily_protein")
    private Double dailyProtein;

    @Column(name = "daily_fats")
    private Double dailyFats;

    @Column(name = "daily_carbs")
    private Double dailyCarbs;

    @Column(name = "recommended_water")
    private Double recommendedWater;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

