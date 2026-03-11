package com.ankap.platform.productservice.controller;

import com.ankap.platform.productservice.entity.Product;
import com.ankap.platform.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from PostgreSQL database...");
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(@PathVariable Long id) {
        log.info("Fetching product {} from PostgreSQL database...", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }

    @PostMapping
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(@RequestBody Product product, @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getSubject();
        product.setSellerId(userEmail);
        return productRepository.save(product);
    }
}