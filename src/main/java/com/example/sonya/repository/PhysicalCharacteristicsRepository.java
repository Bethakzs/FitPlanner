package com.example.sonya.repository;

import com.example.sonya.entity.PhysicalCharacteristics;
import com.example.sonya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhysicalCharacteristicsRepository extends JpaRepository<PhysicalCharacteristics, Long> {
    
    Optional<PhysicalCharacteristics> findByUser(User user);
    
    void deleteByUser(User user);
}

