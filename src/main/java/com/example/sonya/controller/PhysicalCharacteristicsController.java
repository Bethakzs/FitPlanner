package com.example.sonya.controller;

import com.example.sonya.dto.physical.PhysicalCharacteristicsDto;
import com.example.sonya.entity.User;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.PhysicalCharacteristicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/physical-characteristics")
@RequiredArgsConstructor
public class PhysicalCharacteristicsController {

    private final PhysicalCharacteristicsService physicalCharacteristicsService;
    private final UserRepository userRepository;

    @PostMapping("/calculate")
    public ResponseEntity<PhysicalCharacteristicsDto> calculateMyCharacteristics(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        
        if (user.getWeight() == null || user.getHeight() == null || 
            user.getAge() == null || user.getGender() == null) {
            throw new RuntimeException("User must have weight, height, age, and gender to calculate characteristics");
        }
        
        PhysicalCharacteristicsDto characteristics = physicalCharacteristicsService.calculateAndSave(user);
        
        return ResponseEntity.ok(characteristics);
    }

    @GetMapping("/me")
    public ResponseEntity<PhysicalCharacteristicsDto> getMyCharacteristics(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        PhysicalCharacteristicsDto characteristics = physicalCharacteristicsService.getByUser(user);
        
        return ResponseEntity.ok(characteristics);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
    }
}
