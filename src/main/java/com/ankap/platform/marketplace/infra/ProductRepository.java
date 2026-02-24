package com.ankap.platform.marketplace.infra;

import com.ankap.platform.marketplace.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}