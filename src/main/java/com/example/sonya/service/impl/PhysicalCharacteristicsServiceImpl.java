package com.example.sonya.service.impl;

import com.example.sonya.dto.physical.PhysicalCharacteristicsDto;
import com.example.sonya.entity.PhysicalCharacteristics;
import com.example.sonya.entity.User;
import com.example.sonya.enums.BMICategory;
import com.example.sonya.enums.Gender;
import com.example.sonya.mapper.PhysicalCharacteristicsMapper;
import com.example.sonya.repository.PhysicalCharacteristicsRepository;
import com.example.sonya.service.PhysicalCharacteristicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhysicalCharacteristicsServiceImpl implements PhysicalCharacteristicsService {

    private final PhysicalCharacteristicsRepository characteristicsRepository;
    private final PhysicalCharacteristicsMapper characteristicsMapper;

    @Override
    public double calculateBMI(double weight, double height) {
        if (height <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Height and weight must be positive");
        }
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    @Override
    public double calculateBMR(User user) {
        if (user.getWeight() == null || user.getHeight() == null || 
            user.getAge() == null || user.getGender() == null) {
            throw new IllegalArgumentException("User must have weight, height, age, and gender");
        }
        
        double bmr;
        if (user.getGender() == Gender.MALE) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }
        
        if (user.getActivityLevel() != null) {
            bmr *= user.getActivityLevel().getMultiplier();
        }
        
        return bmr;
    }

    @Override
    @Transactional
    public PhysicalCharacteristicsDto calculateAndSave(User user) {
        double bmi = calculateBMI(user.getWeight(), user.getHeight());
        double bmr = calculateBMR(user);
        BMICategory category = determineBMICategory(bmi);
        double optimalWeight = calculateOptimalWeight(user.getHeight(), category);
        
        Optional<PhysicalCharacteristics> existingOpt = characteristicsRepository.findByUser(user);
        
        PhysicalCharacteristics characteristics;
        if (existingOpt.isPresent()) {
            characteristics = existingOpt.get();
            characteristics.setBmi(bmi);
            characteristics.setBmr(bmr);
            characteristics.setBmiCategory(category);
            characteristics.setOptimalWeight(optimalWeight);
            characteristics.setUpdatedAt(LocalDateTime.now());
        } else {
            characteristics = PhysicalCharacteristics.builder()
                    .user(user)
                    .bmi(bmi)
                    .bmr(bmr)
                    .bmiCategory(category)
                    .optimalWeight(optimalWeight)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        
        PhysicalCharacteristics saved = characteristicsRepository.save(characteristics);
        return characteristicsMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PhysicalCharacteristicsDto getByUser(User user) {
        PhysicalCharacteristics characteristics = characteristicsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Physical characteristics not found"));
        return characteristicsMapper.toDto(characteristics);
    }

    @Override
    @Transactional
    public PhysicalCharacteristicsDto update(PhysicalCharacteristics characteristics) {
        characteristics.setUpdatedAt(LocalDateTime.now());
        PhysicalCharacteristics updated = characteristicsRepository.save(characteristics);
        return characteristicsMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PhysicalCharacteristics> findByUser(User user) {
        return characteristicsRepository.findByUser(user);
    }

    private BMICategory determineBMICategory(double bmi) {
        if (bmi < 18.5) return BMICategory.UNDERWEIGHT;
        if (bmi < 25.0) return BMICategory.NORMAL;
        if (bmi < 30.0) return BMICategory.OVERWEIGHT;
        return BMICategory.OBESE;
    }

    private double calculateOptimalWeight(double height, BMICategory category) {
        double heightInMeters = height / 100.0;
        double targetBMI = 22.0;

        if (category == BMICategory.UNDERWEIGHT) {
            targetBMI = 20.0;
        } else if (category == BMICategory.OVERWEIGHT || category == BMICategory.OBESE) {
            targetBMI = 24.0;
        }

        return targetBMI * (heightInMeters * heightInMeters);
    }
}

