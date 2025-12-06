package com.example.sonya.dto.diet;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRequest {
    
    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<Long> productIds;
}

