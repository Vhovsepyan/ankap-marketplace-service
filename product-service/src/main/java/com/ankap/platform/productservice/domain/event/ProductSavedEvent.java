package com.ankap.platform.productservice.domain.event;

import com.ankap.platform.productservice.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable snapshot of a product as it was persisted. Published after a create
 * or an update so that listeners (cache, projections) never see the pre-persist
 * state of a mutable domain object.
 */
public record ProductSavedEvent(UUID id,
                                String sku,
                                String name,
                                BigDecimal price,
                                int availableQuantity,
                                String category,
                                Long version,
                                Boolean isDeleted) {

    public static ProductSavedEvent from(Product product) {
        return new ProductSavedEvent(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getAvailableQuantity(),
                product.getCategory(),
                product.getVersion(),
                product.getDeleted()
        );
    }

    public Product toProduct() {
        return new Product(id, sku, name, price, availableQuantity, category, version, isDeleted);
    }
}
