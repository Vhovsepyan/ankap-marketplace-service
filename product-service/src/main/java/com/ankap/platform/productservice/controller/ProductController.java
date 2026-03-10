package com.ankap.platform.productservice.controller;

import com.ankap.platform.productservice.entity.Product;
import com.ankap.platform.productservice.repository.ProductRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product, @AuthenticationPrincipal Jwt jwt) {
        // Extract the user's identity (email) from the token!
        String userEmail = jwt.getSubject(); 
        
        product.setSellerId(userEmail);
        return productRepository.save(product);
    }
}