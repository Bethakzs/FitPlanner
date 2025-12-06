package com.example.sonya.service;

import com.example.sonya.dto.product.ProductDto;
import com.example.sonya.dto.product.ProductRequest;
import com.example.sonya.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    ProductDto getProductInfo(Long productId);
    
    ProductDto createProduct(ProductRequest request);
    
    ProductDto updateProduct(Long productId, ProductRequest request);
    
    void deleteProduct(Long productId);
    
    List<ProductDto> getAllProducts();
    
    List<ProductDto> searchByName(String name);
    
    Optional<Product> findById(Long productId);
    
    List<Product> findByName(String name);
}
