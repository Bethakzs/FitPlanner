package com.example.sonya.controller;

import com.example.sonya.dto.diet.DietDto;
import com.example.sonya.dto.diet.DietRequest;
import com.example.sonya.entity.User;
import com.example.sonya.exception.UserNotFound;
import com.example.sonya.repository.UserRepository;
import com.example.sonya.service.DietService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diets")
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<DietDto>> getUserDiets(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        List<DietDto> diets = dietService.getUserDiets(user);
        return ResponseEntity.ok(diets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietDto> getDietById(@PathVariable Long id) {
        DietDto diet = dietService.getDietById(id);
        return ResponseEntity.ok(diet);
    }

    @PostMapping
    public ResponseEntity<DietDto> createDiet(
            @RequestBody @Valid DietRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        DietDto diet = dietService.createDiet(user, request.getProductIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(diet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DietDto> updateDiet(
            @PathVariable Long id,
            @RequestBody @Valid DietRequest request) {
        DietDto diet = dietService.updateDiet(id, request.getProductIds());
        return ResponseEntity.ok(diet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiet(@PathVariable Long id) {
        dietService.deleteDiet(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not found", HttpStatus.NOT_FOUND));
    }
}
