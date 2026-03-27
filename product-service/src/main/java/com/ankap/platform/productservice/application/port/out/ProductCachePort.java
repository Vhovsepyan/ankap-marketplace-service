package com.ankap.platform.productservice.application.port.out;

import com.ankap.platform.productservice.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCachePort {
    Optional<Product> get(UUID id);
    void put(Product product);
    void evict(UUID id);
    List<Product> getAllProducts();
}