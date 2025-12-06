package com.example.sonya.repository;

import com.example.sonya.entity.Recommendation;
import com.example.sonya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    List<Recommendation> findByUserOrderByCreatedAtDesc(User user);
    
    List<Recommendation> findByUserAndType(User user, String type);
    
    List<Recommendation> findTop10ByUserOrderByCreatedAtDesc(User user);
}

