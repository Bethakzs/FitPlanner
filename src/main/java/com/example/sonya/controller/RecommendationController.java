package com.example.sonya.controller;

import com.example.sonya.dto.recommendation.RecommendationDto;
import com.example.sonya.dto.recommendation.RecommendationRequest;
import com.example.sonya.entity.User;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getUserRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        List<RecommendationDto> recommendations = recommendationService.getUserRecommendations(user);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationDto> getRecommendationById(@PathVariable Long id) {
        RecommendationDto recommendation = recommendationService.getRecommendationById(id);
        return ResponseEntity.ok(recommendation);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<RecommendationDto>> getRecommendationsByType(
            @PathVariable String type,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        List<RecommendationDto> recommendations = recommendationService.getUserRecommendationsByType(user, type);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/generate")
    public ResponseEntity<RecommendationDto> generateAutoRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        RecommendationDto recommendation = recommendationService.generateRecommendations(user);
        
        return ResponseEntity.ok(recommendation);
    }

    @PostMapping
    public ResponseEntity<RecommendationDto> createRecommendation(
            @RequestBody @Valid RecommendationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        
        RecommendationDto recommendation = recommendationService.generateRecommendations(
                user,
                request.getType(),
                request.getDescription(),
                request.getExerciseIds()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @PathVariable Long id,
            @RequestBody @Valid RecommendationRequest request) {
        
        RecommendationDto recommendation = recommendationService.updateRecommendation(
                id,
                request.getDescription(),
                request.getExerciseIds()
        );
        
        return ResponseEntity.ok(recommendation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
    }
}
