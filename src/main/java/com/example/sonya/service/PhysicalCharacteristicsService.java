package com.example.sonya.service;

import com.example.sonya.dto.physical.PhysicalCharacteristicsDto;
import com.example.sonya.entity.PhysicalCharacteristics;
import com.example.sonya.entity.User;

import java.util.Optional;

public interface PhysicalCharacteristicsService {
    
    double calculateBMI(double weight, double height);
    
    double calculateBMR(User user);
    
    PhysicalCharacteristicsDto calculateAndSave(User user);
    
    PhysicalCharacteristicsDto getByUser(User user);
    
    PhysicalCharacteristicsDto update(PhysicalCharacteristics characteristics);
    
    Optional<PhysicalCharacteristics> findByUser(User user);
}
