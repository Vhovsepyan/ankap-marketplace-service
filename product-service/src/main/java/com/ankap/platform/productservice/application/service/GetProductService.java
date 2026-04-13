package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.product.GetProductUseCase;
import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;

import java.util.List;
import java.util.UUID;

public class GetProductService implements GetProductUseCase {

    private final ProductRepositoryPort repositoryPort;
    private final ProductCachePort cachePort;

    public GetProductService(ProductRepositoryPort repositoryPort, ProductCachePort cachePort) {
        this.repositoryPort = repositoryPort;
        this.cachePort = cachePort;
    }

    @Override
    public Product getProduct(UUID id) {
        return cachePort.get(id)
                .orElseGet(() -> {
                    Product product = repositoryPort.findById(id)
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    cachePort.put(product);
                    return product;
                });
    }

    @Override
    public List<Product> getAllProducts() {
        return repositoryPort.getAllProducts();
    }

}