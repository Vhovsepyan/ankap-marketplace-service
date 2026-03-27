package com.ankap.platform.productservice.dto;

import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, UUID productId, Integer quantity) {
}