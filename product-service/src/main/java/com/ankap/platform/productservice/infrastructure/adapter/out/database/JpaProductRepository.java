package com.ankap.platform.productservice.infrastructure.adapter.out.database;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Modifying
    @Query(value = "DELETE FROM products WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") UUID id);
}