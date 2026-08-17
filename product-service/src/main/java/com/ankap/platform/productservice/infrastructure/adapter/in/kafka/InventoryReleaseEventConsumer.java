package com.ankap.platform.productservice.infrastructure.adapter.in.kafka;

import com.ankap.platform.productservice.application.port.in.ReleaseInventoryCommand;
import com.ankap.platform.productservice.application.port.in.ReleaseInventoryUseCase;
import com.ankap.platform.productservice.dto.InventoryReleaseRequestedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReleaseEventConsumer {

    private final ReleaseInventoryUseCase releaseInventoryUseCase;

    public InventoryReleaseEventConsumer(ReleaseInventoryUseCase releaseInventoryUseCase) {
        this.releaseInventoryUseCase = releaseInventoryUseCase;
    }

    @KafkaListener(
            topics = "inventory-release-topic",
            groupId = "product-service-group",
            containerFactory = "inventoryReleaseListenerContainerFactory")
    @Retry(name = "optimisticLockingRetry")
    public void handleInventoryReleaseRequested(InventoryReleaseRequestedEvent event) {
        releaseInventoryUseCase.release(new ReleaseInventoryCommand(
                event.orderId(),
                event.productId(),
                event.quantity()
        ));
    }
}
