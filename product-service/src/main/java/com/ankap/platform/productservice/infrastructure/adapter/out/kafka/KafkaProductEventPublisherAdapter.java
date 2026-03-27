package com.ankap.platform.productservice.infrastructure.adapter.out.kafka;

import com.ankap.platform.productservice.application.port.out.ProductEventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaProductEventPublisherAdapter implements ProductEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_INVENTORY_RESERVED = "inventory-reserved-topic";
    private static final String TOPIC_INVENTORY_FAILED = "inventory-failed-topic";

    public KafkaProductEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishInventoryReservedEvent(UUID orderId) {
        kafkaTemplate.send(TOPIC_INVENTORY_RESERVED, orderId.toString(), orderId);
    }

    @Override
    public void publishInventoryFailedEvent(UUID orderId, String reason) {
        kafkaTemplate.send(TOPIC_INVENTORY_FAILED, orderId.toString(), reason);
    }
}