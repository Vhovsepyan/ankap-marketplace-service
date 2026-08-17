package com.ankap.platform.orderservice.application.service;

import com.ankap.platform.orderservice.application.port.in.UpdateOrderStatusUseCase;
import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateOrderStatusService.class);
    private final OrderRepositoryPort repositoryPort;

    public UpdateOrderStatusService(OrderRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void cancelOrder(UUID orderId, String reason) {
        Order order = repositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.isSettled()) {
            log.debug("Order {} is already {}; not cancelling again", orderId, order.getStatus());
            return;
        }

        order.cancel();
        repositoryPort.save(order);
        log.warn("Order {} CANCELLED: {}", orderId, reason);
    }
}
