package com.ankap.platform.orderservice.application.service;

import com.ankap.platform.orderservice.application.port.in.UpdateOrderStatusUseCase;
import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.domain.Order;

import java.util.UUID;

public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort repositoryPort;

    public UpdateOrderStatusService(OrderRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void completeOrder(UUID orderId) {
        Order order = repositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.complete();
        repositoryPort.save(order);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        Order order = repositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.cancel();
        repositoryPort.save(order);
    }
}