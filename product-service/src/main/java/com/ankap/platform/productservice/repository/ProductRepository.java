package com.ankap.platform.productservice.repository;

import com.ankap.platform.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}