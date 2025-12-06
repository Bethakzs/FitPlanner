package com.example.sonya.dto.user;

import com.example.sonya.enums.ActivityLevel;
import com.example.sonya.enums.Gender;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDataRequest {
    
    private String firstName;
    
    private String lastName;
    
    @Positive(message = "Age must be positive")
    private Integer age;
    
    private Gender gender;
    
    @Positive(message = "Height must be positive")
    private Double height;
    
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    private ActivityLevel activityLevel;
}

