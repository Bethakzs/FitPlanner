package com.example.sonya.dto.report;

import com.example.sonya.enums.ActivityLevel;
import com.example.sonya.enums.AllergyType;
import com.example.sonya.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMetricsRequest {

    @NotNull(message = "Weight is required")
    @Min(value = 20, message = "Weight must be at least 20 kg")
    private Double weight;

    @NotNull(message = "Height is required")
    @Min(value = 100, message = "Height must be at least 100 cm")
    private Double height;

    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Water intake is required")
    @Min(value = 0, message = "Water intake cannot be negative")
    private Double waterIntake;

    private Set<AllergyType> allergies;

    private Boolean saveReport = false;
}

