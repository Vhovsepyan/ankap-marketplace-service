package com.ankap.platform.productservice.application.port.in.product;

import com.ankap.platform.productservice.domain.Product;

public interface CreateProductUseCase {
    Product save(Product product);
}
