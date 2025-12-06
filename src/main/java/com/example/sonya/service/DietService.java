package com.example.sonya.service;

import com.example.sonya.dto.diet.DietDto;
import com.example.sonya.entity.User;

import java.util.List;

public interface DietService {
    
    DietDto createDiet(User user, List<Long> productIds);
    
    DietDto updateDiet(Long dietId, List<Long> productIds);
    
    DietDto getDietById(Long dietId);
    
    List<DietDto> getUserDiets(User user);
    
    void deleteDiet(Long dietId);
}
