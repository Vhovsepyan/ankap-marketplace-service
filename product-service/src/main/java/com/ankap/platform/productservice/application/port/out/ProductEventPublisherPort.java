package com.ankap.platform.productservice.application.port.out;

import java.util.UUID;

public interface ProductEventPublisherPort {
    void publishInventoryReservedEvent(UUID orderId);
    void publishInventoryFailedEvent(UUID orderId, String reason);
}