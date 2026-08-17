package com.ankap.platform.orderservice.infrastructure.adapter.in.kafka;

import com.ankap.platform.orderservice.application.port.in.PayForOrderUseCase;
import com.ankap.platform.orderservice.application.port.in.UpdateOrderStatusUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Both inventory topics are keyed by order id; only the failure topic carries a
 * meaningful value (the reason). Reading the id from the key rather than the
 * value keeps this independent of how product-service serializes its payloads.
 */
@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final PayForOrderUseCase payForOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public InventoryEventConsumer(PayForOrderUseCase payForOrderUseCase,
                                  UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.payForOrderUseCase = payForOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @KafkaListener(topics = "inventory-reserved-topic", groupId = "order-service-group")
    public void handleInventoryReserved(ConsumerRecord<String, Object> record) {
        UUID orderId = UUID.fromString(record.key());
        log.info("Inventory reserved for order {}. Taking payment through LedgerFlow...", orderId);
        payForOrderUseCase.payFor(orderId);
    }

    @KafkaListener(topics = "inventory-failed-topic", groupId = "order-service-group")
    public void handleInventoryFailed(ConsumerRecord<String, Object> record) {
        UUID orderId = UUID.fromString(record.key());
        String reason = String.valueOf(record.value());
        log.error("Inventory reservation failed for order {}: {}", orderId, reason);
        // Nothing was deducted, so there is no stock to release.
        updateOrderStatusUseCase.cancelOrder(orderId, "inventory reservation failed: " + reason);
    }
}
