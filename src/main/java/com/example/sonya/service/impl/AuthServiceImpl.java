package com.example.sonya.service.impl;

import com.example.sonya.dto.user.AuthRequest;
import com.example.sonya.dto.user.AuthResponse;
import com.example.sonya.dto.user.UserRequest;
import com.example.sonya.entity.PasswordResetToken;
import com.example.sonya.entity.User;
import com.example.sonya.enums.Role;
import com.example.sonya.exception.AuthenticationException;
import com.example.sonya.repository.PasswordResetTokenRepository;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.AuthService;
import com.example.sonya.service.EmailService;
import com.example.sonya.service.JwtService;
import com.example.sonya.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_TOKEN_MISSING = "Refresh token is missing";
    private static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request, HttpServletResponse response) {
        authenticateUser(request.getEmail(), request.getPassword());
        User user = findUserByEmail(request.getEmail());
        return createAuthResponseAndSetCookie(user, response);
    }

    @Override
    @Transactional
    public AuthResponse register(UserRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = createNewUser(request);
        userRepository.save(user);
        
        return createAuthResponseAndSetCookie(user, response);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);
        
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthenticationException(INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));

        if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
            throw new AuthenticationException("Refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        return createAuthResponseAndSetCookie(user, response);
    }

    @Override
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);
        
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthenticationException(INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));
        
        user.setRefreshToken("");
        userRepository.save(user);
        
        cookieUtil.clearRefreshTokenCookie(response);
    }

    private void authenticateUser(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found", HttpStatus.NOT_FOUND));
    }

    private User createNewUser(UserRequest request) {
        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.ROLE_USER);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdDate(Instant.now().toEpochMilli())
                .modifiedDate(Instant.now().toEpochMilli())
                .build();

        user.setRoles(roles);
        return user;
    }

    private AuthResponse createAuthResponseAndSetCookie(User user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        cookieUtil.setRefreshTokenCookie(response, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .roles(user.getRoleValues())
                .build();
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthenticationException(REFRESH_TOKEN_MISSING, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        
        passwordResetTokenRepository.save(resetToken);

        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    token,
                    user.getFirstName() + " " + user.getLastName()
            );
        } catch (Exception e) {
            throw new AuthenticationException("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Invalid reset token", HttpStatus.BAD_REQUEST));

        if (resetToken.isUsed()) {
            throw new AuthenticationException("Reset token already used", HttpStatus.BAD_REQUEST);
        }

        if (resetToken.isExpired()) {
            throw new AuthenticationException("Reset token expired", HttpStatus.BAD_REQUEST);
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setModifiedDate(Instant.now().toEpochMilli());
        
        user.setRefreshToken("");
        
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
} 