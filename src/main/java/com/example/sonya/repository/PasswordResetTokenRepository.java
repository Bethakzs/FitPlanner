package com.example.sonya.repository;

import com.example.sonya.entity.PasswordResetToken;
import com.example.sonya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);
    
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    void deleteByUser(User user);
}

