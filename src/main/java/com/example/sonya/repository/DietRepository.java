package com.example.sonya.repository;

import com.example.sonya.entity.Diet;
import com.example.sonya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    
    List<Diet> findByUserOrderByCreatedAtDesc(User user);
    
    List<Diet> findTop10ByUserOrderByCreatedAtDesc(User user);
}

