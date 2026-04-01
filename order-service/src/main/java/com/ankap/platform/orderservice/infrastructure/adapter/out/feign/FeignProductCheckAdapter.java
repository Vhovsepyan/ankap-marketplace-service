package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;

import com.ankap.platform.orderservice.application.port.out.ProductCheckPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class FeignProductCheckAdapter implements ProductCheckPort {

    private final ProductClient productClient;

    public FeignProductCheckAdapter(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public boolean hasEnoughInventory(UUID productId, int quantity) {
        ProductDto product = productClient.getProductById(productId);
        return product != null && product.availableQuantity() >= quantity;
    }

    @Override
    public BigDecimal getProductPrice(UUID productId) {
        return productClient.getProductById(productId).price();
    }
}