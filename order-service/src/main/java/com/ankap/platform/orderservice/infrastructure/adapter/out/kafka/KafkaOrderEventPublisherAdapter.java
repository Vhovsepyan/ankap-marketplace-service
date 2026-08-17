package com.ankap.platform.orderservice.infrastructure.adapter.out.kafka;

import com.ankap.platform.orderservice.application.port.out.OrderEventPublisherPort;
import com.ankap.platform.orderservice.domain.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaOrderEventPublisherAdapter implements OrderEventPublisherPort {

    private static final String TOPIC_ORDER_EVENTS = "order-events";
    private static final String TOPIC_INVENTORY_RELEASE = "inventory-release-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderPlacedEvent(Order order) {
        kafkaTemplate.send(TOPIC_ORDER_EVENTS, order.getId().toString(), orderPayload(order));
    }

    @Override
    public void publishInventoryReleaseEvent(Order order) {
        kafkaTemplate.send(TOPIC_INVENTORY_RELEASE, order.getId().toString(), orderPayload(order));
    }

    // Sending as a Map to match what Jackson deserializes cleanly on the other end
    private Map<String, Object> orderPayload(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId().toString());
        event.put("productId", order.getProductId().toString());
        event.put("quantity", order.getQuantity());
        return event;
    }
}
