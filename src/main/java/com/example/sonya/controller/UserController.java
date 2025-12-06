package com.example.sonya.controller;

import com.example.sonya.dto.user.UpdateUserDataRequest;
import com.example.sonya.dto.user.UserDto;
import com.example.sonya.entity.User;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUserData(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        UserDto dto = userService.getUserData(user.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUserData(
            @RequestBody @Valid UpdateUserDataRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        UserDto updatedUser = userService.updateUserData(user.getId(), request);
        
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserData(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromDetails(userDetails);
        userService.deleteUser(user.getId());
        
        return ResponseEntity.noContent().build();
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
    }
}
