package com.example.sonya.service;

import com.example.sonya.dto.user.UpdateUserDataRequest;
import com.example.sonya.dto.user.UserDto;
import com.example.sonya.entity.User;

import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    UserDto updateUserData(Long userId, UpdateUserDataRequest request);
    
    UserDto getUserData(Long userId);
    
    Optional<User> findByEmail(String email);
    
    void deleteUser(Long userId);
}
