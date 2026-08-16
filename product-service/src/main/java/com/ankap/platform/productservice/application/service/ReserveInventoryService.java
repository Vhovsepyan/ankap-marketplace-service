package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.ReserveInventoryCommand;
import com.ankap.platform.productservice.application.port.in.ReserveInventoryUseCase;
import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.application.port.out.ProductEventPublisherPort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReserveInventoryService implements ReserveInventoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReserveInventoryService.class);
    private final ProductRepositoryPort repositoryPort;
    private final ProductEventPublisherPort eventPublisherPort;
    private final ProductCachePort cachePort;

    public ReserveInventoryService(ProductRepositoryPort repositoryPort,
                                   ProductEventPublisherPort eventPublisherPort,
                                   ProductCachePort cachePort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.cachePort = cachePort;
    }

    @Override
    public void reserve(ReserveInventoryCommand command) {
        try {
            Product product = repositoryPort.findById(command.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.deductInventory(command.quantity());
            Product savedProduct = repositoryPort.save(product);

            // The save above runs in its own transaction and is already committed here, so
            // refreshing the cache directly is safe and keeps availableQuantity in sync.
            // A cache outage must never turn a successful reservation into a failed one.
            try {
                cachePort.put(savedProduct);
            } catch (Exception cacheFailure) {
                log.error("Failed to refresh cache after inventory reservation for product {}",
                        savedProduct.getId(), cacheFailure);
            }

            eventPublisherPort.publishInventoryReservedEvent(command.orderId());
        } catch (Exception e) {
            eventPublisherPort.publishInventoryFailedEvent(command.orderId(), e.getMessage());
        }
    }
}