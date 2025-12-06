package com.example.sonya.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token, String recipientName);
}

