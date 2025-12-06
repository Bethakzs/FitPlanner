package com.example.sonya.mapper;

import com.example.sonya.dto.product.ProductDto;
import com.example.sonya.dto.product.ProductRequest;
import com.example.sonya.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .calories(product.getCalories())
                .protein(product.getProtein())
                .fats(product.getFats())
                .carbohydrates(product.getCarbohydrates())
                .servingSize(product.getServingSize())
                .build();
    }

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        
        return Product.builder()
                .name(request.getName())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .fats(request.getFats())
                .carbohydrates(request.getCarbohydrates())
                .servingSize(request.getServingSize())
                .build();
    }

    public Product updateFromDto(Product product, ProductRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getCalories() != null) {
            product.setCalories(request.getCalories());
        }
        if (request.getProtein() != null) {
            product.setProtein(request.getProtein());
        }
        if (request.getFats() != null) {
            product.setFats(request.getFats());
        }
        if (request.getCarbohydrates() != null) {
            product.setCarbohydrates(request.getCarbohydrates());
        }
        if (request.getServingSize() != null) {
            product.setServingSize(request.getServingSize());
        }
        
        return product;
    }
}

