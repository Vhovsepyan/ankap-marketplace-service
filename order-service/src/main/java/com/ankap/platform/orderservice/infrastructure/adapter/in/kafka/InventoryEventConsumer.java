package com.ankap.platform.orderservice.infrastructure.adapter.in.kafka;

import com.ankap.platform.orderservice.application.port.in.UpdateOrderStatusUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public InventoryEventConsumer(UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @KafkaListener(topics = "inventory-reserved-topic", groupId = "order-service-group")
    public void handleInventoryReserved(String orderIdStr) {
        log.info("Inventory reserved successfully. Completing order: {}", orderIdStr);
        updateOrderStatusUseCase.completeOrder(UUID.fromString(orderIdStr));
    }

    @KafkaListener(topics = "inventory-failed-topic", groupId = "order-service-group")
    public void handleInventoryFailed(String orderIdStr) {
        log.error("Inventory reservation failed. Cancelling order: {}", orderIdStr);
        updateOrderStatusUseCase.cancelOrder(UUID.fromString(orderIdStr));
    }
}