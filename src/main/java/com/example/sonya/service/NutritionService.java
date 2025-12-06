package com.example.sonya.service;

import com.example.sonya.dto.report.NutritionReportResponse;
import com.example.sonya.dto.report.UserMetricsRequest;

import java.util.List;

public interface NutritionService {
    
    NutritionReportResponse generateReport(UserMetricsRequest request, String userEmail);
    
    List<NutritionReportResponse> getUserReports(String userEmail);
}

