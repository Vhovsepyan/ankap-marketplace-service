package com.ankap.platform.orderservice.application.port.out;

import com.ankap.platform.orderservice.domain.Order;

public interface OrderEventPublisherPort {
    void publishOrderPlacedEvent(Order order);
}