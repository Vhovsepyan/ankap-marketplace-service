package com.ankap.platform.productservice.application.port.in.product;

import java.util.UUID;

public interface DeleteProductUseCase {

    void softDeleteProductById(UUID productId);

    void hardDeleteProductById(UUID productId);
}
