package com.example.sonya.repository;

import com.example.sonya.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    Optional<Exercise> findByName(String name);
    
    List<Exercise> findByType(String type);
    
    List<Exercise> findByNameContainingIgnoreCase(String name);
}

