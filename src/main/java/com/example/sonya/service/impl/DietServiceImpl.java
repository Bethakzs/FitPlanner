package com.example.sonya.service.impl;

import com.example.sonya.dto.diet.DietDto;
import com.example.sonya.entity.Diet;
import com.example.sonya.entity.Product;
import com.example.sonya.entity.User;
import com.example.sonya.mapper.DietMapper;
import com.example.sonya.repository.DietRepository;
import com.example.sonya.repository.ProductRepository;
import com.example.sonya.service.DietService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietServiceImpl implements DietService {

    private final DietRepository dietRepository;
    private final ProductRepository productRepository;
    private final DietMapper dietMapper;

    @Override
    @Transactional
    public DietDto createDiet(User user, List<Long> productIds) {
        List<Product> products = productIds.stream()
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + id)))
                .collect(Collectors.toList());
        
        double totalCalories = products.stream().mapToDouble(Product::getCalories).sum();
        double totalProtein = products.stream().mapToDouble(Product::getProtein).sum();
        double totalFats = products.stream().mapToDouble(Product::getFats).sum();
        double totalCarbs = products.stream().mapToDouble(Product::getCarbohydrates).sum();
        
        String balanceDescription = String.format(
            "Protein: %.1fg, Fats: %.1fg, Carbs: %.1fg",
            totalProtein, totalFats, totalCarbs
        );
        
        Diet diet = Diet.builder()
                .user(user)
                .products(products)
                .totalCalories(totalCalories)
                .totalProtein(totalProtein)
                .totalFats(totalFats)
                .totalCarbs(totalCarbs)
                .balanceDescription(balanceDescription)
                .createdAt(LocalDateTime.now())
                .build();
        
        Diet savedDiet = dietRepository.save(diet);
        return dietMapper.toDto(savedDiet);
    }

    @Override
    @Transactional
    public DietDto updateDiet(Long dietId, List<Long> productIds) {
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new RuntimeException("Diet not found"));
        
        List<Product> products = productIds.stream()
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + id)))
                .collect(Collectors.toList());
        
        double totalCalories = products.stream().mapToDouble(Product::getCalories).sum();
        double totalProtein = products.stream().mapToDouble(Product::getProtein).sum();
        double totalFats = products.stream().mapToDouble(Product::getFats).sum();
        double totalCarbs = products.stream().mapToDouble(Product::getCarbohydrates).sum();
        
        String balanceDescription = String.format(
            "Protein: %.1fg, Fats: %.1fg, Carbs: %.1fg",
            totalProtein, totalFats, totalCarbs
        );
        
        diet.setProducts(products);
        diet.setTotalCalories(totalCalories);
        diet.setTotalProtein(totalProtein);
        diet.setTotalFats(totalFats);
        diet.setTotalCarbs(totalCarbs);
        diet.setBalanceDescription(balanceDescription);
        diet.setUpdatedAt(LocalDateTime.now());
        
        Diet updatedDiet = dietRepository.save(diet);
        return dietMapper.toDto(updatedDiet);
    }

    @Override
    @Transactional(readOnly = true)
    public DietDto getDietById(Long dietId) {
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new RuntimeException("Diet not found"));
        return dietMapper.toDto(diet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietDto> getUserDiets(User user) {
        return dietRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(dietMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDiet(Long dietId) {
        dietRepository.deleteById(dietId);
    }
}
