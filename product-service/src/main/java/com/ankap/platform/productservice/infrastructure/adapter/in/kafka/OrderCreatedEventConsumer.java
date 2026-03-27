package com.ankap.platform.productservice.infrastructure.adapter.in.kafka;

import com.ankap.platform.productservice.application.port.in.ReserveInventoryCommand;
import com.ankap.platform.productservice.application.port.in.ReserveInventoryUseCase;
import com.ankap.platform.productservice.dto.OrderPlacedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private final ReserveInventoryUseCase reserveInventoryUseCase;

    public OrderCreatedEventConsumer(ReserveInventoryUseCase reserveInventoryUseCase) {
        this.reserveInventoryUseCase = reserveInventoryUseCase;
    }

    @KafkaListener(topics = "order-events", groupId = "product-service-group") // Make sure topic matches Order Service!
    @Retry(name = "optimisticLockingRetry")
    public void handleOrderCreatedEvent(OrderPlacedEvent event) {
        ReserveInventoryCommand command = new ReserveInventoryCommand(
                event.orderId(),
                event.productId(),
                event.quantity()
        );
        reserveInventoryUseCase.reserve(command);
    }
}