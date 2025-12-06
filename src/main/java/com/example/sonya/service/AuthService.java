package com.example.sonya.service;

import com.example.sonya.dto.user.AuthRequest;
import com.example.sonya.dto.user.AuthResponse;
import com.example.sonya.dto.user.UserRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request, HttpServletResponse response);
    
    AuthResponse register(UserRequest request, HttpServletResponse response);

    AuthResponse refreshToken(String refreshToken, HttpServletResponse response);
    
    void logout(String refreshToken, HttpServletResponse response);
    
    void forgotPassword(String email);
    
    void resetPassword(String token, String newPassword);
} 