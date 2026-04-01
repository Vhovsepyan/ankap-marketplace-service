package com.ankap.platform.orderservice.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;


public interface ProductCheckPort {
    boolean hasEnoughInventory(UUID productId, int quantity);
    BigDecimal getProductPrice(UUID productId);
}