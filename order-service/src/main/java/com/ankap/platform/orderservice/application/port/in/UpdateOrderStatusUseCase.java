package com.ankap.platform.orderservice.application.port.in;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {

    void cancelOrder(UUID orderId, String reason);
}
