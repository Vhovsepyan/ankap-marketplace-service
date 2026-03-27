package com.ankap.platform.productservice.infrastructure.adapter.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
}