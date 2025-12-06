package com.example.sonya.repository;

import com.example.sonya.entity.NutritionReport;
import com.example.sonya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionReportRepository extends JpaRepository<NutritionReport, Long> {
    
    List<NutritionReport> findByUserOrderByCreatedAtDesc(User user);
    
    List<NutritionReport> findTop10ByUserOrderByCreatedAtDesc(User user);
}

