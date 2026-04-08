package com.ankap.platform.productservice.config;

import com.ankap.platform.productservice.application.port.in.GetProductUseCase;
import com.ankap.platform.productservice.application.port.in.ReserveInventoryUseCase;
import com.ankap.platform.productservice.application.port.in.product.DeleteProductUseCase;
import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.application.port.out.ProductEventPublisherPort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.application.service.DeleteProductService;
import com.ankap.platform.productservice.application.service.GetProductService;
import com.ankap.platform.productservice.application.service.ReserveInventoryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductServiceConfig {

    @Bean
    public GetProductUseCase getProductUseCase(ProductRepositoryPort repositoryPort, ProductCachePort cachePort) {
        return new GetProductService(repositoryPort, cachePort);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepositoryPort repositoryPort, ApplicationEventPublisher eventPublisher) {
        return new DeleteProductService(repositoryPort, eventPublisher);
    }

    @Bean
    public ReserveInventoryUseCase reserveInventoryUseCase(ProductRepositoryPort repositoryPort, ProductEventPublisherPort eventPublisherPort) {
        return new ReserveInventoryService(repositoryPort, eventPublisherPort);
    }
}