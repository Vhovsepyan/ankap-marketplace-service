package com.ankap.platform.productservice.application.port.out;

import com.ankap.platform.productservice.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Optional<Product> findById(UUID id);
    Product save(Product product);
    List<Product> getAllProducts();
}