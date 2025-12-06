package com.example.sonya.controller;

import com.example.sonya.dto.report.NutritionReportResponse;
import com.example.sonya.dto.report.UserMetricsRequest;
import com.example.sonya.entity.User;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.NutritionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;
    private final UserRepository userRepository;

    @PostMapping("/report/complete")
    public ResponseEntity<NutritionReportResponse> generateCompleteReport(
            @RequestBody @Valid UserMetricsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        NutritionReportResponse response = nutritionService.generateCompleteReport(
            request, 
            userDetails.getUsername()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/report")
    public ResponseEntity<NutritionReportResponse> generateReport(
            @RequestBody @Valid UserMetricsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        NutritionReportResponse response = nutritionService.generateReport(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<NutritionReportResponse>> getUserReports(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NutritionReportResponse> reports = nutritionService.getUserReports(userDetails.getUsername());
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        nutritionService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
    }
}
