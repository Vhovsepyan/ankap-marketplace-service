package com.ankap.platform.orderservice.application.port.out;

import com.ankap.platform.orderservice.domain.Order;

public interface OrderEventPublisherPort {

    void publishOrderPlacedEvent(Order order);

    /** Compensation: hand the reserved stock back after a payment did not succeed. */
    void publishInventoryReleaseEvent(Order order);
}
