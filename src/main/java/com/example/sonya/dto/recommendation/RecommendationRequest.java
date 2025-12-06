package com.example.sonya.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private List<Long> exerciseIds;
}

