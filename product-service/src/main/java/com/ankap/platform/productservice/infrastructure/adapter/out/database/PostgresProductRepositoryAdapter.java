package com.ankap.platform.productservice.infrastructure.adapter.out.database;

import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresProductRepositoryAdapter implements ProductRepositoryPort {

    private final JpaProductRepository jpaRepository;

    public PostgresProductRepositoryAdapter(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(entity -> new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getAvailableQuantity(),
                entity.getCategory(),
                entity.getVersion()
        ));
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = new ProductEntity(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getAvailableQuantity(),
                product.getCategory(),
                product.getVersion()
        );
        ProductEntity saved = jpaRepository.save(entity);
        return new Product(
                saved.getId(),
                saved.getSku(),
                saved.getName(),
                saved.getPrice(),
                saved.getAvailableQuantity(),
                saved.getCategory(),
                saved.getVersion()
        );
    }

    @Override
    public List<Product> getAllProducts() {
        return jpaRepository.findAll().stream()
                .map(entity -> new Product(
                        entity.getId(),
                        entity.getSku(),
                        entity.getName(),
                        entity.getPrice(),
                        entity.getAvailableQuantity(),
                        entity.getCategory(),
                        entity.getVersion()
                ))
                .toList();
    }

    @Override
    public void softDeleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void hardDeleteById(UUID id) {
        jpaRepository.hardDeleteById(id);
    }
}