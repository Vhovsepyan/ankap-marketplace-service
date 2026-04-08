package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.product.DeleteProductUseCase;
import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.event.ProductDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class DeleteProductService implements DeleteProductUseCase {
    private final ProductRepositoryPort repositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteProductService(ProductRepositoryPort repositoryPort, ApplicationEventPublisher eventPublisher) {
        this.repositoryPort = repositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void softDeleteProductById(UUID productId) {
        repositoryPort.softDeleteById(productId);
        eventPublisher.publishEvent(new ProductDeletedEvent(productId));
    }

    @Override
    @Transactional
    public void hardDeleteProductById(UUID productId) {
        repositoryPort.hardDeleteById(productId);
        eventPublisher.publishEvent(new ProductDeletedEvent(productId));
    }
}
