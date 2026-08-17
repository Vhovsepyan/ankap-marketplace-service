package com.ankap.platform.productservice.dto;

import java.util.UUID;

public record InventoryReleaseRequestedEvent(UUID orderId, UUID productId, Integer quantity) {
}
