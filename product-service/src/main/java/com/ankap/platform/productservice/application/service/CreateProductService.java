package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.product.CreateProductUseCase;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;
import com.ankap.platform.productservice.domain.event.ProductSavedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

public class CreateProductService implements CreateProductUseCase {
    private final ProductRepositoryPort repositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    public CreateProductService(ProductRepositoryPort repositoryPort, ApplicationEventPublisher eventPublisher) {
        this.repositoryPort = repositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Product save(Product product) {
        Product savedProduct = repositoryPort.save(product);
        eventPublisher.publishEvent(ProductSavedEvent.from(savedProduct));
        return savedProduct;
    }
}
