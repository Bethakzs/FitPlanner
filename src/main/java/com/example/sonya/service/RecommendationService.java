package com.example.sonya.service;

import com.example.sonya.dto.recommendation.RecommendationDto;
import com.example.sonya.entity.User;

import java.util.List;

public interface RecommendationService {
    
    RecommendationDto generateRecommendations(User user, String type, String description, List<Long> exerciseIds);
    
    RecommendationDto generateRecommendations(User user);
    
    RecommendationDto getRecommendationById(Long recommendationId);
    
    List<RecommendationDto> getUserRecommendations(User user);
    
    List<RecommendationDto> getUserRecommendationsByType(User user, String type);
    
    RecommendationDto updateRecommendation(Long recommendationId, String description, List<Long> exerciseIds);
    
    void deleteRecommendation(Long recommendationId);
}
