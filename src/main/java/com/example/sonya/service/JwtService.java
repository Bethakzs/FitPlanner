package com.example.sonya.service;

import com.example.sonya.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(User user);
    boolean isAccessTokenValid(String token, UserDetails userDetails);
    boolean isRefreshTokenValid(String token, User user);
    String extractUsername(String token);
} 