package com.ankap.platform.orderservice.dto;

import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, UUID productId, Integer quantity) {
}