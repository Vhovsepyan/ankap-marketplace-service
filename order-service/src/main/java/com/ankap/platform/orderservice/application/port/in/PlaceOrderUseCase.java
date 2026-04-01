package com.ankap.platform.orderservice.application.port.in;

import com.ankap.platform.orderservice.domain.Order;

public interface PlaceOrderUseCase {
    Order placeOrder(PlaceOrderCommand command);
}