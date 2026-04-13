package com.ankap.platform.productservice.application.port.in.product;

import com.ankap.platform.productservice.domain.Product;

import java.util.List;
import java.util.UUID;

public interface GetProductUseCase {
    Product getProduct(UUID id);

    List<Product> getAllProducts();
}