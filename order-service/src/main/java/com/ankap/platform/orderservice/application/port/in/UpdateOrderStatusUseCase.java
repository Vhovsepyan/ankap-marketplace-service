package com.ankap.platform.orderservice.application.port.in;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    void completeOrder(UUID orderId);
    void cancelOrder(UUID orderId);
}