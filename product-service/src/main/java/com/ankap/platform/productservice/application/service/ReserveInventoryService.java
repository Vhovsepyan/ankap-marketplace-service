package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.ReserveInventoryCommand;
import com.ankap.platform.productservice.application.port.in.ReserveInventoryUseCase;
import com.ankap.platform.productservice.application.port.out.ProductEventPublisherPort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;

public class ReserveInventoryService implements ReserveInventoryUseCase {

    private final ProductRepositoryPort repositoryPort;
    private final ProductEventPublisherPort eventPublisherPort;

    public ReserveInventoryService(ProductRepositoryPort repositoryPort, ProductEventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public void reserve(ReserveInventoryCommand command) {
        try {
            Product product = repositoryPort.findById(command.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.deductInventory(command.quantity());
            repositoryPort.save(product);

            eventPublisherPort.publishInventoryReservedEvent(command.orderId());
        } catch (Exception e) {
            eventPublisherPort.publishInventoryFailedEvent(command.orderId(), e.getMessage());
        }
    }
}